import React from 'react';

class App extends React.Component {
   render() {
      return (
      <div>
      <h1>Header</h1>
        <h2>Content</h2>
        <p>This is the content!!!</p>
		<h1> Hellooo , {formatName(user)} </h1>
		        </div>
      );
   }
}

function formatName(user)
{
  return user.firstName + ' ' + user.lastName;
}

const user = {
  firstName: 'Pillliiii',
  lastName: 'cat '
};

/*
const element = (
  <h2>
    Hello, {formatName(user)}!!!!
  </h2>
);
*/

export default App;

