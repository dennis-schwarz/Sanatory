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
   	}
   	translate <-200, -200, 0> // translate to origin
   	scale <-1, 1, 1> // mirror x-axis
   	rotate <0, 0, 180> // rotate in the ground plane
   	rotate <-135, 0, 0> // tilt towards the camera
}