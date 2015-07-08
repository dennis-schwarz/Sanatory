//------------------------------------------------------------------------
// clock - textures ------------------------------------------------------
#declare Frame_Texture =
	texture {
		pigment {
			color rgb <1.0, 1, 1> * 0.1
		}
		finish {
			phong 1
			reflection {
				0.40
				metallic
			}
		}
	}

#declare H_Texture =
	texture {
		pigment {
			color rgb <1, 1, 1> * 0.0
		}
	}

#declare Min_Texture =
	texture {
		H_Texture
	}

#declare Face_Texture =
	texture {
		pigment {
			color rgb <1, 1, 1> * 1.10
		}
	}

#declare Hands_Texture =
	texture {
		pigment {
			color rgb <1, 1, 1> * 0.0
		}
	}

//------------------------------------------------------------------------
// clock-size ------------------------------------------------------------
#declare CR = 30;

//------------------------------------------------------------------------
// calculation of time ---------------------------------------------------
#local H = 0;
#local Min = 0;
#declare Initial_Clock = 483840;
#declare Final_Clock = 486719;
#declare totalTime = Final_Clock - Initial_Clock;
#declare tempClock = clock - Initial_Clock;
#declare Clock_Time = (tempClock / 720) + H  / (12) + Min / (720);

// rotations of hands (clock) --------------------------------------------
#declare Rotate_H = Clock_Time * 360;
#declare Rotate_Min = Clock_Time * 360 * 12;

//minutes-jump (clock) --------------------------------------------------
#declare Rotate_Min = int(Rotate_Min / 6 + 0.001) * 6;
#declare Flat = <1, 1, 0.025>;

// border radii (clock) --------------------------------------------------
#declare Min_Ro = CR * 0.92;
#declare Min_Ri = CR * 0.82;
#declare H_Ri = CR * 0.65;
#declare Min_R = CR * 0.0200;
#declare H_R = CR * 0.0400;
#declare Face_D = 0.001;

// length of hands (clock) -----------------------------------------------
#declare Hand_H_Len = CR * 0.60;
#declare Hand_Min_Len = CR * 0.85;

// radii of the hands (clock) --------------------------------------------
#declare Hand_H_D = CR * 0.055;
#declare Hand_Min_D = CR * 0.035;

// position z of hands (clock) -------------------------------------------
#declare Hand_H_Z = CR * 0.05;
#declare Hand_Min_Z = CR * 0.04;

//------------------------------------------------------------------------
// view ------------------------------------------------------------------
union {
	// hands -------------------------------------------------------------
	// hours
	cylinder {
		<0, 0, 0>, <0, Hand_H_Len, 0>, Hand_H_D
		scale Flat
		rotate <0, 0, - Rotate_H>
		translate <0, 0, -Hand_H_Z>
		texture {
			Hands_Texture
		}
		translate <200, 140, 0>
		no_shadow
	}

	// minutes
	cylinder {
		<0, 0, 0>, <0, Hand_Min_Len, 0>, Hand_Min_D
		scale Flat
		rotate <0, 0, - Rotate_Min>
		translate <0, 0, -Hand_Min_Z>
		texture {
			Hands_Texture
		}
		translate <200, 140, 0>
		no_shadow
	}

	// body --------------------------------------------------------------
	// center-point
	cylinder {
		<0, 0, -Hand_H_Z>, <0, 0, 0>, Hand_H_D * 1.5
		texture {
			Hands_Texture
		}
		translate <200, 140, 0>
		no_shadow
	}

	// face
	cylinder {
		<0, 0, 0>, <0, 0, 0.001>, CR
		texture {
			Face_Texture
		}
		translate <200, 140, 0>
		no_shadow
		no_shadow
	}

// clock-face
	#declare Nr = 0;
	#while (Nr < 60)
		#if(Nr / 5 = int(Nr / 5)) // hours
			cylinder {
				<0, H_Ri, 0>, <0, Min_Ro, 0>, H_R
				scale Flat
				rotate <0, 0, Nr * 360 / 60>
				texture {
					H_Texture
				}
				translate <200, 140, 0>
		no_shadow
			}
		#else // minutes
			cylinder {
				<0, Min_Ri, 0>, <0, Min_Ro, 0>, Min_R
				scale Flat
				rotate <0, 0, Nr * 360 / 60>
				texture {
					Min_Texture
				}
				translate <200, 140, 0>
		no_shadow
			}
		#end
		#declare Nr = Nr + 1;
	#end
}

