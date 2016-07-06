'use strict';

import gulp from 'gulp';
import jscs from 'gulp-jscs';
import jshint from 'gulp-jshint';

gulp.task('jscs', function() {
  return gulp.src(['./index.js'])
    .pipe(jscs())
    .pipe(jscs.reporter())
    .pipe(jscs.reporter('fail'));
});

gulp.task('jshint', function() {
  return gulp.src('./index.js')
    .pipe(jshint('.jshintrc'))
    .pipe(jshint.reporter('jshint-stylish'))
    .pipe(jshint.reporter('fail'));
});

gulp.task('default', ['jscs']);
