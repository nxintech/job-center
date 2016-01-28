var gulp = require('gulp');
var webpack = require('gulp-webpack');
var rename = require('gulp-rename');
var uglify=require("gulp-uglify");

var path = "../../job-center-server/src/main/webapp/resources/script";
gulp.task("dev",function()
{
  gulp.src("./index.js").pipe(webpack({module:{loaders:[{ test: /\.js$/, loader: 'babel' ,query: {presets: ['react', 'es2015']},exclude: /node_modules/}]}})).pipe(rename("index.js")).pipe(gulp.dest(path));
});

gulp.task("prod",function()
{
  gulp.src("./index.js").pipe(webpack({module:{loaders:[{ test: /\.js$/, loader: 'babel' ,query: {presets: ['react', 'es2015']},exclude: /node_modules/}]}})).pipe(uglify()).pipe(rename("index.js")).pipe(gulp.dest(path));
});