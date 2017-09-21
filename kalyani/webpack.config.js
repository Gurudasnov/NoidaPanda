var config = {
   entry: './view/main.js',
	
   output: {
      path:__dirname,
      filename: './view/index.js',
   },
	
   devServer: {
      inline: true,
      port: 8092
   },
	
   module: {
      loaders: [
         {
            test: /\.jsx?$/,
            exclude: /node_modules/,
            loader: 'babel-loader',
				
            query: {
               presets: ['es2015', 'react']
            }
         }
      ]
   }
}

module.exports = config;