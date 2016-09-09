# SimLib

SimLib is flexible and extensible framework for implementing and comparing semantic similarity and relatedness metrics for Knowledge Graphs.

SimLib takes advantage of the last innovations introduced by Java 8, such as support for functional programming, lambda expressions, data streams processing and advanced parallelization, making easier to develop and maintain a software architecture able to deal with a huge amount of data.

This framework defines a data model to properly represent graph entities and also a guideline on how similarity or relatedness metrics should be implemented, making use of the efficient parallelization paradigm provided by the Java 8 API.

Moreover, SimLib is able to interface with [ABSTAT](http://abstat.disco.unimib.it), a summarization framework developed by the [ITIS group](http://siti-server01.siti.disco.unimib.it/itislab) at [University of Milano-Bicocca](http://www.unimib.it) that helps Linked Data consumers to make sense of big and complex datasets, extracting ontology-based data abstraction models.

Credits
------------

This library was originally developed by [Giorgio Basile](https://github.com/giorgiobasile).

The [graph kernel package](src/main/java/it/poliba/sisinflab/simlib/neighborhood/) was originally developed by Corrado Magarelli.

This project have been developed under the supervision of:
   * [Prof. Tommaso Di Noia](http://sisinflab.poliba.it/dinoia)
   * [Paolo Tomeo](http://sisinflab.poliba.it/tomeo)
   * [Azzurra Ragone, Ph.D.](http://sisinflab.poliba.it/ragone)

Contacts
------------
- Tommaso Di Noia, tommaso [dot] dinoia [at] poliba [dot] it  
- Paolo Tomeo, paolo [dot] tomeo [at] poliba [dot] it  
- Azzurra Ragone, azzurra [dot] ragone [at] unimib [dot] it	
- Giorgio Basile, giorgio [dot] basile4 [at] gmail [dot] com	
- Corrado Magarelli, c9magare [at] gmail [dot] com
