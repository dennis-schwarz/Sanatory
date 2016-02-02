//------------------------------------------------------------------------
// general settings ------------------------------------------------------
#version 3.7;
global_settings {
	assumed_gamma 1.0
}
#include "usb_geom.inc" // usb model
#include "patients.inc" // patients
#include "clock.inc" // clock
#include "ground.inc" // ground
#include "sun.inc" // sun
#include "ambient" // ambient

#declare rendered_Interval = Final_Clock - Initial_Clock;

//------------------------------------------------------------------------
// camera ----------------------------------------------------------------
camera {
	location <0, 0, -500>
  	look_at <0, 0, 0>
  	angle 50
}

//-----------------------------------------------------------------------
// assemble and position buildings and patients -------------------------
object {
	union {
      		object {
				usb
			}
      		object {
				patients
			}
			
			//object {
				//sun
			//}
			
			object {
				ground
			}
   	}
   	translate <-200, -200, 0> // translate to origin
   	scale <-1, 1, 1> // mirror x-axis
   	rotate <0, 0, clock * 180 / rendered_Interval> // rotate in the ground plane
   	rotate <-135, 0, 0> // tilt towards the camera
}