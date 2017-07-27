import React, { Component } from 'react';

class ListItem extends Component{
render(){
    return(
<option  className='bg-info' value={this.props.word.name} >{this.props.word.name}</option>
    )
}
}
export default ListItem;