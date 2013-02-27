#ifdef ISENSE_JENNIC

#include <isense/config.h>
#include <isense/os.h>
#include <isense/application.h>
#include <isense/task.h>
#include <isense/uart.h>
#include <isense/modules/amr_module/amr_module.h>
#include <isense/modules/gateway_module/gateway_module.h>
#include <isense/button_handler.h>
#include <isense/util/ishell_interpreter.h>
#include <isense/data_handlers.h>

#define INDOOR

#ifdef INDOOR
	//------------------------------------------------------------------------
	// Below the sensor timing configuration is defined:
	// this is a good indoor setting:
	// - sample every 5 ms, and average 4 of these values, put the result to the buffer
	// - like this, each value handed out to the app is averaged over 20 ms, which is one 50 Herz period
	// - the buffer is handed over to the app when it is completely full (i.e. contains 25 averaged samples
	// - like this, the app will receive 2 buffers/second
	//------------------------------------------------------------------------

	// interval in ms between 2 sensor samples to be taken
	#define SAMPLE_INTERVAL 5
	// number of sensor samples to be averaged to one value handed over to the application
	#define SAMPLE_COUNT 10
	// number of sensor samples taken before buffer is handed over to the app
	// this value must not exceed the maximum buffer size of 25
	#define BUFFER_SIZE 20
	//------------------------------------------------------------------------
#else
	//------------------------------------------------------------------------
	// This is an alternative sensor timing configuration:
	// this is an outdoor setting optimized for low energy consumption:
	// - sample only every 100 ms (no averaging), as this should be fast anought to notice most vehicles,
	//   as a vehicle traveling at 50km/h makes about 14m/s, and the modules range is approx. 5m
	// - the buffer is handed over to the app when it contains 20 samples
	// - like this, the app will receive a buffer every 2 seconds
	//------------------------------------------------------------------------
	// interval in ms between 2 sensor samples to be taken
	#define SAMPLE_INTERVAL 100
	// number of sensor samples to be averaged to one value handed over to the application
	#define SAMPLE_COUNT 1
	// number of sensor samples taken before buffer is handed over to the app
	// this value must not exceed the maximum buffer size of 25
	#define BUFFER_SIZE 20
#endif


using namespace isense;

//----------------------------------------------------------------------------
class AmrModuleDemoApplication :
	public Application, 
	public Task,
	public UartPacketHandler,
	public BufferDataHandler,
	public AmrAlarmHandler,
	public SleepHandler
{

public: 
	AmrModuleDemoApplication(Os &os);
 	bool stand_by (void); // Memory held
 	void wake_up (bool memory_held);
	void execute( void* userdata );
	void boot();
	void set_gain(bool high);
	void handle_uart_packet( uint8 type, uint8* buf, uint8 length );
	void handle_buffer_data( BufferData* buf );
	void handle_alarm( uint16 alarm_bits[2] );
	
private:
	//pointer to sensor driver
	AmrModule* amr_;
	// abstraction class for communication with the PC
	IShellInterpreter* isi_;
	// Driver for Core Module
	CoreModule* cm_;
};

//----------------------------------------------------------------------------
AmrModuleDemoApplication::
AmrModuleDemoApplication(Os &os) :
	Application(os),
	amr_(NULL),
	isi_(NULL),
	cm_(NULL)
{
}

//----------------------------------------------------------------------------
// this function is called upon booting the sensor node
void 
AmrModuleDemoApplication::
boot () // Memory held
{
	os_.debug("Booting AMR demo application...");
	// instantiate Core Module (used to flash the LED here to indicate sleep/wake state
	cm_ = new CoreModule(os());
	// turn on LED, as node is currently awake
	cm_->led_on();

	// register as a sleep handler, to receive sleep/wake events
	os().add_sleep_handler(this);

	// Prevent the node from sleeping
	// This is done here only to be ready to receive commands from iShell via the UART
	// It is not required for the operation of the sensor itself
	os().allow_sleep(false);

	// instantiate the AMR module driver, and set yourself as buffer handler and alarm handler
	amr_ = new AmrModule(os(), SAMPLE_COUNT, SAMPLE_INTERVAL, BUFFER_SIZE);
	// set yourself as buffer handler and alarm handler
	// like this, you will be delivered all sensor readings with alternating buffers
	// through calls of handle_buffer_data
	amr_->set_handler(this);
	// set yourself as alarm handler
	// like this, the app will receive alarm events, indicating that a vehicle was detected
	// in this case, handle_alarm will be called
	amr_->set_alarm_handler(this);

	// instantiate an iShell interpreter, used for sending buffer data to iShell
	// if the "Curve illustrator (single window)" plugin is active, it will display the sensor data
	isi_ = new IShellInterpreter(os());

	// enable uart interrupts, and set app as packet handler for type 3
	// this this, you can use the "Messenger" plugin of iShell to send command to control the app
	// send 03 00 to set the AMR module gain to low
	// send 03 01 to set the AMR module gain to high
	// send 03 02 to calibrate the AMR module
	os().uart(0).enable_interrupt(true);
	os().uart(0).set_packet_handler(03, this);
	
	// enable AMR Module
	amr_->enable();
	// initiate AMR module calibration
	// this must be done after enabling the module,
	// after moving the module, if sensor values go mad, and from time to time
	// to compensate for temperature drift and sensor saturation
	// if the calibration is successful, the sensor should deliver values around 2000
	// after the calibration if no magnetic fields are present
	os().debug("Enabled sensor, calibrating...");
	amr_->set_gain(false);
	amr_->calibrate();

	// start sensor
	os().debug("Done. Starting sensor.");
	amr_->start();

}

//----------------------------------------------------------------------------
// this function is called when the sensor node wakes from sleep mode
void 
AmrModuleDemoApplication::
wake_up (bool memory_held)
{
	// turn on Core Module LED when node wakes
	cm_->led_on();
}

//----------------------------------------------------------------------------
// this function is called when the sensor node goes to sleep
bool 
AmrModuleDemoApplication::
stand_by () // Memory held
{
	// turn off Core Module LED when node goes to sleep
	cm_->led_off();
	return true;
}

//----------------------------------------------------------------------------
// This function is called as a consequence of calling os().add_task(this, NULL);
// here, the sensor is calibrated, after the according command was received via
// UART from iShell (see constructor for details)
void 
AmrModuleDemoApplication::
execute( void* userdata )
{
	// stop sensor before calibration
	amr_->stop();
	os().debug("Calibrating AMR sensor...");
	amr_->calibrate();
	os().debug("Done");
	// start sensor again
	amr_->start();
}

//----------------------------------------------------------------------------
// this function is called when the AMR module has filled one buffer
// the data is handed over in a BufferData structure
// For details about the BufferData structure, please refer to the API doc
// at www.coalesenses.com/doxygen
void
	AmrModuleDemoApplication::
	handle_buffer_data( BufferData* buf )
{
	// hand over data to iShell to display it the "Curve illustrator (single window)" plugin
	isi_->send_buffer_data(buf, os().id(), SAMPLE_COUNT * SAMPLE_INTERVAL);
	// Here, additional reasoning based upon the buffer data could be done
	// E.g. Vehicle detection
	uint16 alrm_bits[2] = { 0, abs(2000 - buf->buf[1])};
	if (alrm_bits[1] > 1000)
		handle_alarm(alrm_bits);
}
//----------------------------------------------------------------------------
// This method is called if the node receives data of type 03 via the UART
void
	AmrModuleDemoApplication::
	handle_uart_packet( uint8 type, uint8* buf, uint8 length )
{
	if ((type == 3)&&(length>=1))
	{
		if (buf[0] == 0) {
			os().debug("Gain set low");
			amr_->set_gain(false);
			os_.add_task( this, NULL);
		} else if (buf[0] == 1) {
			os().debug("Gain set high");
			amr_->set_gain(true);
			os_.add_task( this, NULL);
		} else if (buf[0] == 2) {
			// add a task for calibrating the AMR sensor (takes quite long)
			os_.add_task( this, NULL);
		} else if (buf[0] == 3) {
			os().debug("Channel one - true");
			amr_->flip(0, true);
			os_.add_task( this, NULL);
		} else if (buf[0] == 4) {
			os().debug("Channel one - false");
			os_.add_task( this, NULL);
			amr_->flip(0, false);
		} else if (buf[0] == 5) {
			os().debug("Channel two - true");
			os_.add_task( this, NULL);
			amr_->flip(1, true);
		} else if (buf[0] == 6) {
			os().debug("Channel two - false");
			os_.add_task( this, NULL);
			amr_->flip(1, false);
		} else if (buf[0] == 7) {
			os().debug("Flipping both channels");
			amr_->flip();
			os_.add_task( this, NULL);
		}
	}
}

//----------------------------------------------------------------------------
// This method is called if the node receives data of type 03 via the UART
void
AmrModuleDemoApplication::
handle_alarm( uint16 alarm_bits[2] )
{
	if (alarm_bits[0] == 0)
		os().debug("AMR vehicle detected (%d)", alarm_bits[1]);
}

//----------------------------------------------------------------------------
// This method is called when the OS boots, to create an application instance
Application* 
application_factory(Os &os) 
{
		return new AmrModuleDemoApplication(os);
}

#endif
