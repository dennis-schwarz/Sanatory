//------------------------------------------------------------------------
// sun -------------------------------------------------------------------
// calculate variables for sun movement
#declare rendered_interval = Final_Clock - Initial_Clock; // calculate rendered interval
#declare days = (rendered_interval) / 1439; // number of rendered days
#declare step = (clock - Initial_Clock) / (rendered_interval);  // calculate step size
#declare clock_convert_x = days * 360 * step; // number of days * 360, sine of this value used for x coordinate
#declare clock_convert_y = clock_convert_x - 90; // same as clock_convert_x but decreased by 90. Sine of this value used for y Coordinate
#declare sinus_x = sin(radians(clock_convert_x)); // sine is used to create cyclic in- and decreasing of x- and y-coordinates
#declare sinus_y = sin(radians(clock_convert_y));                     
#declare x_max = 100; // max values of x- and y-coordinates
#declare y_max = 100;
#declare sun_x = x_max * sinus_x;
#declare sun_y = y_max * sinus_y;
#declare sun_z = 0;

// let the sunshine in
//#declare sun =
	#if(sinus_y >= 0) // sun light sources is only activated if above horizon (y >= 0)
		light_source {
			<sun_x, sun_y, sun_z>
  			color rgb 1
  			parallel
  			point_at <0, 0, 0>
		}
	#end