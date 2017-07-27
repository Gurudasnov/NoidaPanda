

import React, { Component } from 'react';
import ListItem from './ListItem';


class FilterList extends Component{
    
constructor(){
super();
this.state={
    search: ''
};

}
updateSearch(event){
this.setState({search:event.target.value.substr(0,20)});
}

change(event){

         document.getElementById("avi").value=event.target.value;
     }
render(){
    
    let filteredWords=this.props.words.filter(
        (word)=>{
            return word.name.toLowerCase().indexOf(this.state.search.toLowerCase())!=-1;
        }
    );
    return(


        <div className='main-div'>
            
             <link rel='stylesheet' type='text/css' href='scroll.css'/>
            
            
            <input id="avi" type="text" className='myInput' value={this.state.search} onChange={this.updateSearch.bind(this)}/>
                
				
<select   size="8" className='bg-info' onChange={this.change.bind(this)} >
    {filteredWords.map((word)=>{
 
        return <ListItem word={word} key={word.id}/>
		}
    )
    }
    </select>
  
            </div>

    )
}
}
export default FilterList;