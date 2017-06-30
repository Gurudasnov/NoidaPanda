Installing Node JS and Executing the application.

1. Create project folder and start your project by using “npm init” cause its good practice. This wizard will ask you some question like name, version, test script etc and at the end you will have your package.json file ready.

2. To install mongoose run following command.
	npm install --save mongoose
	
3. Here is my package.json.
	package.json
	{
		"name": "nodeMongo",
		"version": "1.0.0",
		"description": "Node.js and MongoDb tutorial.",
		"main": "Server.js",
		"scripts": {
			"test": "mocha"
		},
		"repository": {
			"type": "git",
			"url": "https://github.com/codeforgeek/Node-and-mongo-tutorial"
		},
		"keywords": [
			"Node.js",
			"mongoDb"
		],
		"author": "Shahid Shaikh",
		"license": "ISC",
		"bugs": {
			"url": "https://github.com/codeforgeek/Node-and-mongo-tutorial/issues"
		},
		"homepage": "https://github.com/codeforgeek/Node-and-mongo-tutorial",
		"dependencies": {
			"body-parser": "^1.13.3",
			"express": "^4.13.3",
			"mongoose": "^4.1.2"
		}
	}
	
4. You can copy this and create your package file and run following command to install the modules.
	npm install

5. Setting up our Server.
	To set up our project Server, we are going to use Express module in server.js

6. Let’s run our code and see how it’s behaving. Run the code using following command.
	npm start

7. We have set up our Server successfully. 
