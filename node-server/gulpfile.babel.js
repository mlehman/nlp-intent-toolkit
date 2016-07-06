'use strict';

import gulp from 'gulp';
import jscs from 'gulp-jscs';
import jshint from 'gulp-jshint';

gulp.task('jscs', function() {
	return gulp.src(['./public/js/src/refac-script.js', './public/js/src/iframe.js'])
		.pipe(jscs())
		.pipe(jscs.reporter());
});

gulp.task('default', ['jscs']);

