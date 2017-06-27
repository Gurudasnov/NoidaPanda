var express     =   require("express");
var app         =   express();
var bodyParser  =   require("body-parser");
var router      =   express.Router();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({"extended" : false}));
app.use( ( req, res, next ) => {
    setTimeout(next, 30000 );
});

router.get("/",function(req,res){

    res.json({"error" : false,"message" : "Hello World"});
});

app.use('/',router);

app.listen(3000);
console.log("Listening to PORT 3000");
