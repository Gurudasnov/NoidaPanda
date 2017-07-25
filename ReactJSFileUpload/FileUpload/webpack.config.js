var config = {
   entry: './main.js',
	
   output: {
	   filename: 'index.js'
   },
	
   devServer: {
      inline: true,
      port: 3000
   },
   module: {
      loaders: [
         {
            test: /\.jsx?$/,
            exclude: /node_modules/,
            loader: 'babel',
				
            query: {
               presets: ['es2015', 'react']
            }
         }
      ]
   },
   externals:['axios'],
	resolve: { 
        extensions: [ '.js', '.jsx'],  
    alias: {
      'js-data-angular': '../dist/js-data-angular.js'
    } 
}

}

module.exports = config;
