# BrainBase
Platform for running and interfacing with neural networks.<br>
<br>
A connectome is represented as a .net file, which stores neuron names and their associated connections. All .net files should take the following form, where  is the newline character (MAKE SURE TO START THE FILE WITH A NEWLINE):<br>
<br>
<br>
neuron_name1<br>
connection1 weight<br>
connection2 weight<br>
connection3 weight<br>
connection4 weight<br>
<br>
neuron_name2<br>
connection1 weight<br>
connection2 weight<br>
connection3 weight<br>
<br>
No optimization algorithms are included at this point.<br>
<br>
The current configuration is a C Elegans roundworm connectome which asynchronously pings IP addresses generated from energy accumulations in the first 32 ventral/dorsal muscles. Positive responses to these pings trigger firing of the "food" neurons. This configuration is based on the work of Timothy Busbice (github.com/Connectome/GoPiGo) and Gabriel Garrett (github.com/ggaabe/GoPiGo).
