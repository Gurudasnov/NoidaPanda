var path = require('path');
var webpack = require('webpack');

var config = {
    entry: './src/main.js',
  
    output: {
        filename: 'index.js'
    },
  
    devServer: {
        inline: true,
        port: 3000
    },

    module: {
        loaders: [
        {
            test: /\.(js|jsx)$/,
            exclude: /node_modules/,
            loader: 'babel-loader',
        
            query: {
                presets: ['es2015', 'react', 'stage-0']
            }
        }]
    },
}

module.exports = config; 