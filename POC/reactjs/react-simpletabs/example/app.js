/** @jsx React.DOM */
'use strict';

var Tabs = ReactSimpleTabs;
var App = React.createClass({
  onMount: function(selectedIndex, $selectedPanel, $selectedTabMenu) {
    console.log('on mount, showing tab ' + selectedIndex);
  },
  onBeforeChange: function(selectedIndex, $selectedPanel, $selectedTabMenu) {
    console.log('before the tab ' + selectedIndex);
  },
  onAfterChange: function(selectedIndex, $selectedPanel, $selectedTabMenu) {
    console.log('after the tab ' + selectedIndex);
  },
  render: function() {
    return (
      <Tabs tabActive={2} onBeforeChange={this.onBeforeChange} onAfterChange={this.onAfterChange} onMount={this.onMount}>
        <Tabs.Panel title='Home'>
      <div class="tabstyle">
          <h2>
          This the blog where you can share you can start writing on the topics of youur interest....
          </h2>
      </div>
        </Tabs.Panel>
        <Tabs.Panel title='Authors'>
		 <div class="tabstyle">
          <h2>Explore topics based on Authors  </h2>
		    </div>
        </Tabs.Panel>
        <Tabs.Panel title='Topics'>
		<div class="tabstyle" style="width=20 height=20">
          <h2>Explore the writings based on the topics </h2>
		     </div>
        </Tabs.Panel>
      </Tabs>
    );
  }
});

React.renderComponent(<App />, document.getElementById('tabs'));
