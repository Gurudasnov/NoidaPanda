var fs = require("fs");
var path = require('path');
var async = require('async');
var cmd=require('node-cmd');
 
exports.fileprint = function(req,res){
  // console.log("req",req.files);
  var filesArray = req.files;
        async.each(filesArray,function(file,eachcallback){
         //carry out your file operations here and pass callback at the end  
         },function(err){
          if(err){
              console.log("error ocurred in each",err);
          }
          else{
            console.log("finished prcessing");
            res.send({
                      "code":"200",
                      "success":"files printed successfully"
                      })
            cmd.run('rm -rf ./fileupload/*');
          }
          });
}