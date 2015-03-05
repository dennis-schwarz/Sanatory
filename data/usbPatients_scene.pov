//=========================
//Four makros for smooth start and stop
#macro Cos_01(X)
	(0.5 - 0.5 * cos(pi * X))
#end
//————————————-------------
#macro Cos_02(X)
	(0.5 - 0.5 * cos(2 * pi * X))
#end
//————————————-------------
#macro Cos_03(X)
	(1 - (0.5 - 0.5 * cos(pi * X)))
#end
//————————————-------------
#macro Cos_01(X)
	(1 - (0.5 - 0.5 * cos(2 * pi * X)))
#end
//————————————-------------

//=========================
//The moving spheres (patients)
union {

	//Sphere {
		<0.0, 0.0, 0.0>, 5

		texture {
			pigment {
				rgb <1, 0, 0>
			}

			finish{
				diffuse 0.9
				phong 1
			}
		}//End of texture

		//translate <0.0, 0.0, 0.0>
		//translate <0.0 * Cos_01(clock), 0.0 * Cos_01(clock), 0.0 * Cos_01(clock)>
	}//End of sphere
	//-------------------------

	//Sphere {
		<92.0, 183.0, 16.25>, 5

		texture {
			pigment {
				rgb <1, 0, 0>
			}

			finish{
				diffuse 0.9
				phong 1
			}
		}//End of texture

		//translate <92.0, 183.0, 16.25>
		//translate <92.0 * Cos_01(clock), 183.0 * Cos_01(clock), 16.25 * Cos_01(clock)>
	}//End of sphere
	//-------------------------

	//Sphere {
		<92.0, 183.0, 16.25>, 5

		texture {
			pigment {
				rgb <1, 0, 0>
			}

			finish{
				diffuse 0.9
				phong 1
			}
		}//End of texture

		//translate <92.0, 183.0, 16.25>
		//translate <92.0 * Cos_01(clock), 183.0 * Cos_01(clock), 16.25 * Cos_01(clock)>
	}//End of sphere
	//-------------------------

	//Sphere {
		<92.0, 183.0, 16.25>, 5

		texture {
			pigment {
				rgb <1, 0, 0>
			}

			finish{
				diffuse 0.9
				phong 1
			}
		}//End of texture

		//translate <92.0, 183.0, 16.25>
		//translate <92.0 * Cos_01(clock), 183.0 * Cos_01(clock), 16.25 * Cos_01(clock)>
	}//End of sphere
	//-------------------------

	//Sphere {
		<125.0, 186.0, 16.25>, 5

		texture {
			pigment {
				rgb <1, 0, 0>
			}

			finish{
				diffuse 0.9
				phong 1
			}
		}//End of texture

		//translate <125.0, 186.0, 16.25>
		//translate <125.0 * Cos_01(clock), 186.0 * Cos_01(clock), 16.25 * Cos_01(clock)>
	}//End of sphere
	//-------------------------

	//Sphere {
		<92.0, 183.0, 16.25>, 5

		texture {
			pigment {
				rgb <1, 0, 0>
			}

			finish{
				diffuse 0.9
				phong 1
			}
		}//End of texture

		//translate <92.0, 183.0, 16.25>
		//translate <92.0 * Cos_01(clock), 183.0 * Cos_01(clock), 16.25 * Cos_01(clock)>
	}//End of sphere
	//-------------------------

}
//End of union
//=========================