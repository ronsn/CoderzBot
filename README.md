
## This readme is not done. It is still work in progress.

Features may not work as expected. This is alpha software!

Visit our IRC channel (german/english): [![Visit our IRC channel](https://kiwiirc.com/buttons/irc.freenode.net/coderzhome.png)](https://kiwiirc.com/client/irc.freenode.net/?&theme=cli#coderzhome)

# What is CoderzService?

It is an IRC-Bot. If you don't know what "IRC" means this software will most likely be useless for you.
The name is inherited from the channel it is mainly designed for: #coderzhome on freenode.

# How is CoderzService different from other bots?

First of all, it is written in java, including the commands which are realized as plugins.

But a more important thing is: It is aimed to be an interactive learning program.

The learning module is still in pre-alpha stage though.

# What libraries are used?

- PircBotX: The heart of the bot, wraps all IRC-Communication.
- Jetty: The Webserver will be used to administer the bot.
- MySQL: Will be used to store data (obviously)

These three are the main libs, everything else ist more or less there to help. Take a look in the libs folder!

# How does all this stuff work?

Since there are constant changes at the moment, this will be answered at a later time.

# How do I write plugins?

See above.


Some hints:

 - After compiling change the directory and start the bot with:
<code>java -Dfile.encoding=UTF8 -jar net.freenode.xenomorph.xenomat.jar</code>
- DSL-reconnects will most likely kill the bot! Be aware of this if you aren't running the bot behind a permanent connection.