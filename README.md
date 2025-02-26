# ChatGPT plugins quickstart

Get a todo list ChatGPT plugin up and running in under 5 minutes using Java. If you do not already have plugin developer access, please [join the waitlist](https://openai.com/waitlist/plugins).

## About this port

This example is an attempt to make the most direct minimal port of the original python to Java using JBang + Quarkus. 

It is NOT the most optimal port, it uses raw file access an is not using any of the OpenAPI features Quarkus has available. Might do that in the future :)

## Setup

To run the plugin, enter the following command:

```bash
jbang main.java
```

Once the local server is running:

1. Navigate to https://chat.openai.com. 
2. In the Model drop down, select "Plugins" (note, if you don't see it there, you don't have access yet).
3. Select "Plugin store"
4. Select "Develop your own plugin"
5. Enter in `localhost:5003` since this is the URL the server is running on locally, then select "Find manifest file".

The plugin should now be installed and enabled! You can start with a question like "What is on my todo list" and then try adding something to it as well! 

## Getting help

If you run into issues or have questions building a plugin, please join our [Developer community forum](https://community.openai.com/c/chat-plugins/20).

