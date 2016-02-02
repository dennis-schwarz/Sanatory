//------------------------------------------------------------------------
// ground --------------------------------------------------------
#declare ground = 
plane {
	<0, 0, 1>, 0
}

#declare ground_texture =
texture {
	pigment {
		//color rgb <1.1, 1.3, 1> * 0.2
		checker color <1, 1, 1>
		color rgb <0.6, 0.6, 0.6>
		scale <20, 20, 20>
	}
  	finish {
    		ambient 0.05
    		diffuse 0.5
    		specular 0.9 roughness 0.02
  	}
}

object {
	ground    
   	texture {
		ground_texture
	}
   	translate <0, 0, 7> // lowest floor is 8 m above 0 (was 7)
   	rotate <-137, 0, 0> // tilt towards the camera (was -135)
}