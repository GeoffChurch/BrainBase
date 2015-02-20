# BrainBase
Platform for running and interfacing with neural networks.

A connectome is represented as a .net file, which stores neuron names and their associated connections. All .net files should take the following form, where \n is the newline character (MAKE SURE TO START THE FILE WITH A NEWLINE):

\n
neuron_name1\n
connection1 weight\n
connection2 weight\n
connection3 weight\n
connection4 weight\n
\n
neuron_name2\n
connection1 weight\n
connection2 weight\n
connection3 weight\n

No optimization algorithms are included at this point.

The current configuration is a C Elegans roundworm connectome which asynchronously pings IP addresses generated from energy accumulations in the first 32 ventral/dorsal muscles. Positive responses to these pings trigger firing of the "food" neurons. This configuration is based on the work of Timothy Busbice (github.com/Connectome/GoPiGo) and Gabriel Garrett (github.com/ggaabe/GoPiGo).
