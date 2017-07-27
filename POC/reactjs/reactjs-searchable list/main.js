import React from 'react';
import ReactDOM from 'react-dom';
import App from './App.jsx';
let words=[
    {
 id:1,   
name:'WonderLA',
},
{
    id:2,
name:'HAL',
},
{
    id:3,
name:'Planeterium',
},
{
    id:4,
name:'Safari',
}
,{
 id:5,   
name:'LaalBagh',
},{
 id:6,   
name:'Snow City',
},{
 id:7,   
name:'MG Road',
}
,{
 id:8,   
name:'Brigade Road',
}
,{
 id:9,   
name:'HAL Museum',
}
]

ReactDOM.render(<App words={words}/>, document.getElementById('app'));

