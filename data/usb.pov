//------------------------------------------------------------------------
// POV-Ray 3.7 Scene File "patients.pov"
// created by Manuel Huerbin, Dennis Schwarz and Miriam Scholer, FHNW, 2015

//------------------------------------------------------------------------
// general settings ------------------------------------------------------
#version 3.7;
global_settings {
	assumed_gamma 1.0
}
#include "colors.inc"
#include "textures.inc"
#include "math.inc"
#include "transforms.inc"
#include "usb.pov" //usb-model (transparent)

//------------------------------------------------------------------------
// camera ----------------------------------------------------------------
#declare Camera =
camera {
	perspective
	up <0, 1, 0>
	right -x * image_width / image_height
	location <0, 0, 1092.539>
	look_at <0, 0, 1091.539>
	angle 22.34049 //horizontal FOV angle
	rotate <0, 0, -33.51162> //roll
	rotate <44.54607, 0, 0> //pitch
	rotate <0, -44.05138, 0> //yaw
	translate <201.5, 233.35, 32.205>
}
camera {
	Camera
}

//------------------------------------------------------------------------
// background ------------------------------------------------------------
background {
	color srgb <1, 1, 1>
}

//------------------------------------------------------------------------
// sun -------------------------------------------------------------------
light_source {
	<-1000, 2500, -2500>
	color <1, 1, 1>
}

//------------------------------------------------------------------------
// sky -------------------------------------------------------------------
sky_sphere {
	pigment {
		gradient <0, 0, 0>
		color_map {
			[0 color rgb <1, 1, 1>]
			[1 color rgb <1, 2, 3>]
		}
		scale 2
	}
}

/------------------------------------------------------------------------
// patient --------------------------------------------------------------
#declare Patient =
sphere {
	<1, 1, 1>, 3
	texture {
		pigment {
			color rgb <0, 1, 0>
		}
		finish {
			ambient 0.1
			diffuse 0.85
			phong 1
		}
	}
}

//------------------------------------------------------------------------
// splines ---------------------------------------------------------------
#declare Spline1 =
spline {
	natural spline
	Zeit, <92.0, 183.0, 16.25>,
	Zeit, <92.0, 183.0, 16.25>,
	Zeit, <92.0, 183.0, 16.25>,
	Zeit, <125.0, 186.0, 16.25>,
	Zeit, <92.0, 183.0, 16.25>,
	Zeit, <0.0, 0.0, 0.0>,
}
#declare Spline2 =
spline {
	natural spline
	Zeit, <92.0, 183.0, 16.25>,
	Zeit, <107.0, 271.0, 16.25>,
	Zeit, <125.0, 186.0, 16.25>,
	Zeit, <107.0, 271.0, 16.25>,
	Zeit, <107.0, 271.0, 16.25>,
	Zeit, <107.0, 271.0, 16.25>,
	Zeit, <0.0, 0.0, 0.0>,
}
#declare Spline3 =
spline {
	natural spline
	Zeit, <92.0, 183.0, 16.25>,
	Zeit, <92.0, 183.0, 16.25>,
	Zeit, <92.0, 183.0, 16.25>,
	Zeit, <125.0, 186.0, 16.25>,
	Zeit, <92.0, 183.0, 16.25>,
	Zeit, <0.0, 0.0, 0.0>,
}
#declare Spline4 =
spline {
	natural spline
}

//------------------------------------------------------------------------
// loop ------------------------------------------------------------------
#declare Start = 0; //start
#declare End = 1; //end
#while (Start < End)
	object {
		Patient
		translate Spline1(mod((clock + Start / End), 5))
	}
	object {
		Patient
		translate Spline2(mod((clock + Start / End), 5))
	}
	object {
		Patient
		translate Spline3(mod((clock + Start / End), 5))
	}
	#declare Start = Start + 1; //steps
#end