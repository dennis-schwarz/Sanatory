//------------------------------------------------------------------------
// general settings ------------------------------------------------------
#version 3.7;
global_settings {
	assumed_gamma 1.0
}
//#include "usb_geom.inc" // usb model
#include "patients.inc" // patients
#include "clock.inc" // clock

//------------------------------------------------------------------------
// camera ----------------------------------------------------------------
camera {
	location <0, 0, -500>
  	look_at <0, 0, 0>
  	angle 50
}

//------------------------------------------------------------------------
// lights ----------------------------------------------------------------
light_source {
	<-50, 50, -100>
  	color rgb 1
  	parallel
  	point_at <0, 0, 0>
}

//------------------------------------------------------------------------
// ground --------------------------------------------------------
#declare ground = 
plane {
	<0, 0, 1>, 0
}

#declare ground_texture =
texture {
	pigment {
		checker color <1, 1, 1>
		color <0.6, 0.6, 0.6>
		scale <20, 20, 20>
	}
  	finish {
    		ambient 0.05
    		diffuse 0.5
    		specular 0.9 roughness 0.02
    		//reflection {1 exponent 1.0}
  	}
}

object {
	ground    
   	texture {
		ground_texture
	}
   	translate <0, 0, 7> // lowest floor is 8 m above 0
   	rotate <-135, 0, 0> // tilt towards the camera
}

//-----------------------------------------------------------------------
// assemble and position buildings and patients -------------------------
object {
	union {
      		//object {
				//usb
			//}
      		object {
				patients
			}
   	}
   	translate <-200, -200, 0> // translate to origin
   	scale <-1, 1, 1> // mirror x-axis
   	rotate <0, 0, 180> // rotate in the ground plane
   	rotate <-135, 0, 0> // tilt towards the camera
}