var config = {

   entry: './main.js',

   output: {
     path: __dirname,
      filename: 'index.js',
   },

   devServer: {
      inline: true,
      port: 7079
   
 /*
}, 
 externals: {
  'Config': JSON.stringify(process.env.ENV === 'production' ? {
    serverUrl: "http://localhost:8100/NPO-restCall/rest/json/data"
  } : {
    serverUrl: "http://localhost:8100/NPO-restCall/rest/json/data"
  })
  */
},

   module: {
      loaders: [
         {
            test: /\.(js|jsx)$/,
            exclude: /node_modules/,
            loader: 'babel-loader',

            query: 
			{
               presets: ['es2015', 'react']
            }
         }
      ]
   }
}

module.exports = config;
