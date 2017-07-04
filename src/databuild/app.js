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
          <h2>
          This is the blog where you can share you can start writing on the topics of youur interest....
          </h2>
        </Tabs.Panel>
        <Tabs.Panel title='Authors'>
          <h2>Explore topics based on the author.....</h2>
        </Tabs.Panel>
        <Tabs.Panel title='Topics'>
          <h2>Explore the writings based on the topics..... </h2>
        </Tabs.Panel>
      </Tabs>
    );
  }
});

React.renderComponent(<App />, document.getElementById('tabs'));
