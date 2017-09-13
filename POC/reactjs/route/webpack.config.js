var config = {

   entry: './main.js',

   output: {
     path: __dirname,
      filename: 'index.js',
   },

   devServer: {
      inline: true,
      port: 8084
   },

   module: {
      loaders: [
         {
            test: /\.(js|jsx)$/,
            exclude: /(node_modules|bower_components)/,

            loader: 'babel-loader',

            query: 
			{
               presets: ['es2015', 'react']
            }
         },
		  { test: /\.css-loader$/, loader: "style-loader" }

      ]
	}
}

module.exports = config;
