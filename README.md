kelp_additional_kernels
=========

 [**KeLP**][kelp-site] is the Kernel-based Learning Platform (Filice '15) developed in the [Semantic Analytics Group][sag-site] of the [University of Roma Tor Vergata][uniroma2-site]. 

This project contains several ***kernel functions*** that extend the set of kernels made available in the **kelp-core** project. Moreover, this project implements the specific **representations** required to enable the application of such kernels.

In this project the following kernel functions are considered: 

* Sequence kernels
* Tree kernels
* Graphs kernels

Examples about the usage of the following kernels, please refer to the project **kelp-full**.

KELP is released as open source software under the Apache 2.0 license and the source code is available on [Github][github].


Sequence Kernels
----------------
These kernels measure a sort of similarity between pairs of sequences that is proportional to the number of shared sub-sequences. In particular, a **SequenceRepresentation** is exploited by sequence kernels in kernel-based methods. It models a sequence that can be employed for representing, for example, sequences of words. Each element of a sequence is implemented as a generic structure (**StructureElement**) that can be typed and it can contain also additional information. In this release, the **SequenceKernel** described in (Bunescu '06) is implemented. It operates on SequenceRepresentations evaluating the common subsequences between two sequences.


Tree Kernels
------------
These kernels measure a sort of similarity between pairs of trees that is proportional to the number of shared tree fragments. In particular a **TreeRepresentation** models a tree structure that can be employed for representing syntactic trees. Tree nodes are associated with a generic structure (**StructureElement**) that can be typed and it can contains also additional information. The TreeRepresentation is exploited by tree kernels in kernel-based methods.

In this release, the following Tree Kernels have been implemented:

* **SubTreeKernel**: it is the tree kernel described in (Vishwanathan '03) and optimized in (Moschitti '06a). It operates on TreeRepresentations evaluating the number of common fragments shared by two trees. The considered fragments are complete subtrees, i.e. a node and its entire descendancy.
* **SubSetTreeKernel**: it is the tree kernel described in (Vishwanathan '03) and optimized in (Moschitti '06a). It operates on TreeRepresentations evaluating the number of common fragments shared by two trees. The considered fragments are subset-trees, i.e. a node and its partial descendancy. The descendancy can be incomplete in depth, but no partial productions are allowed; in other words given a node either all its children or none of them must be considered.
* **PartialTreeKernel**: it is the tree kernel described in (Moschitti '06b). It operates on TreeRepresentations evaluating the number of common fragments shared by two trees. The considered fragments are partial subtrees, i.e. a node and its partial descendancy (i.e. partial productions are allowed).
* **SmoothedPartialTreeKernel**  it generalize the PartialTreeKernel by allowing to match those fragments that are not identical but that are semantically related (Croce '11), by relying on the similarity functions between tree nodes, i.e. **StructureElement**. The implementation of node similarity allows to easily extend the notion of similarity between nodes, e.g. allowing to implement more expressive kernels, as the **CompositionallySmoothedPartialTreeKernel** that embeds algebraic operators of Distributional Compositional Semantics (Annesi '14) within trees that reflect the syntactic dependency parse tree of as sentence.


Graph Kernels
-------------
These kernels measure a sort of similarity between pairs of graphs that is proportional to the number of shared subgraphs. In particular **DirectedGraphRepresentation** models a directed unweighted graph structure, i.e, any set of nodes and directed edges connecting them. Edges do not have any weight or label; nodes are associated with a generic structure (**StructureElement**) usually containing a discrete label (it can contain a different and richer form of information as well).

Currently, the following kernels are implemented:

* **ShortestPathKernel**: it is the implementation of the shortest path kernel described in (Borgwardt '05)

* **WLSubtreeMapper**: it extracts a _SparseVector_ from a given graphs. Such _SparseVector_ corresponds to the explicit feature space projection of the WLSubtree Kernel for graphs (Shervashidze '11). The features correspond to the trees resulting from breadth-first visits of depth up to _h_. It is implemented as a _Manipulator_ that enriches an Example with these vectorial representation allowing to exploit them with kernel methods or with linear learning algorithms. 


=============

##Including KeLP in your project

If you want to include this set of kernel functions, you can  easily include it in your [Maven][maven-site] project adding the following repositories to your pom file:

```
<repositories>
	<repository>
			<id>kelp_repo_snap</id>
			<name>KeLP Snapshots repository</name>
			<releases>
				<enabled>false</enabled>
				<updatePolicy>always</updatePolicy>
				<checksumPolicy>warn</checksumPolicy>
			</releases>
			<snapshots>
				<enabled>true</enabled>
				<updatePolicy>always</updatePolicy>
				<checksumPolicy>fail</checksumPolicy>
			</snapshots>
			<url>http://sag.art.uniroma2.it:8081/artifactory/kelp-snapshot/</url>
		</repository>
		<repository>
			<id>kelp_repo_release</id>
			<name>KeLP Stable repository</name>
			<releases>
				<enabled>true</enabled>
				<updatePolicy>always</updatePolicy>
				<checksumPolicy>warn</checksumPolicy>
			</releases>
			<snapshots>
				<enabled>false</enabled>
				<updatePolicy>always</updatePolicy>
				<checksumPolicy>fail</checksumPolicy>
			</snapshots>
			<url>http://sag.art.uniroma2.it:8081/artifactory/kelp-release/</url>
		</repository>
	</repositories>
```

Then, the [Maven][maven-site] dependency for the whole **KeLP** package:

```
<dependency>
    <groupId>it.uniroma2.sag.kelp</groupId>
    <artifactId>kelp-additional-kernels</artifactId>
    <version>2.0.0</version>
</dependency>
```


Alternatively, thanks to the modularity of **KeLP**, you can include one of the following modules:

* [kelp-core](https://github.com/SAG-KeLP/kelp-core): it contains the core interfaces and classes for algorithms, kernels and representations. It contains also the base set of classifiers, regressors and clustering algorithms. It serves as the main module to develop new kernel functions or new algorithms;

* [kelp-additional-algorithms](https://github.com/SAG-KeLP/kelp-additional-algorithms): it contains additional learning algorithms, such as the **KeLP** Java implementation of Liblinear or Online Learning algorithms, such as the Passive Aggressive;

* [kelp-full](https://github.com/SAG-KeLP/kelp-full): it is a complete package of KeLP that contains the entire set of existing modules, i.e. additional  kernel functions and algorithms.

=============
How to cite KeLP
----------------
If you find KeLP usefull in your researches, please cite the following paper:

```
@InProceedings{filice-EtAl:2015:ACL-IJCNLP-2015-System-Demonstrations,
	author = {Filice, Simone and Castellucci, Giuseppe and Croce, Danilo and Basili, Roberto},
	title = {KeLP: a Kernel-based Learning Platform for Natural Language Processing},
	booktitle = {Proceedings of ACL-IJCNLP 2015 System Demonstrations},
	month = {July},
	year = {2015},
	address = {Beijing, China},
	publisher = {Association for Computational Linguistics and The Asian Federation of Natural Language Processing},
	pages = {19--24},
	url = {http://www.aclweb.org/anthology/P15-4004}
}
```
=============

References
----------
 
(Vishwanathan '03) S.V.N. Vishwanathan and A.J. Smola. _Fast kernels on strings and trees_. In Proceedings of Neural Information Processing Systems, 2003.


(Bunescu '06) Razvan Bunescu and Raymond Mooney. _Subsequence kernels for relation extraction_. In Y. Weiss, B. Scholkopf, and J. Platt, editors, Advances in Neural Information Processing Systems 18, pages 171-178. MIT Press, Cambridge, MA, 2006.


(Borgwardt '06) K. M. Borgwardt and H. P. Kriegel, _Shortest-Path Kernels on Graphs_, in Proceedings of the Fifth IEEE International Conference on Data Mining, 2005, pp. 74–81.

(Moschitti '06a) Alessandro Moschitti. _Making Tree Kernels Practical for Natural Language Learning_. In Proceedings of EACL, 2006

(Moschitti '06b) Alessandro Moschitti. _Efficient convolution kernels for dependency and constituent syntactic trees_. In proceeding of European Conference on Machine Learning (ECML) (2006)

(Croce '11) Danilo Croce, Alessandro Moschitti, and Roberto Basili. 2011. _Structured lexical similarity via convolution kernels on dependency trees_. In EMNLP, Edinburgh.

(Shervashidze '11) N. Shervashidze, P. Schweitzer, E. J. van Leeuwen, K. Mehlhorn and K. M. Borgwardt _Weisfeiler-lehman graph kernels_, JMLR, vol. 12, pp. 2539–2561, 2011

(Annesi '14) Paolo Annesi, Danilo Croce, and Roberto Basili. 2014. _Semantic compositionality in tree kernels_. In Proc. of CIKM 2014, pages 1029–1038, New York, NY, USA. ACM.

(Filice '15) Simone Filice, Giuseppe Castellucci, Danilo Croce, Roberto Basili: Kelp: a kernel-based learning platform for natural language processing. In: Proceedings of ACL: System Demonstrations. Beijing, China (July 2015)

============

Usefull Links
-------------

Kelp site: [http://sag.art.uniroma2.it/demo-software/kelp/][kelp-site]

SAG site: [http://sag.art.uniroma2.it] [sag-site]

Source code hosted at GitHub: [https://github.com/SAG-KeLP][github]

[sag-site]: http://sag.art.uniroma2.it "SAG site"
[uniroma2-site]: http://www.uniroma2.it "University of Roma Tor Vergata"
[kelp-site]: http://sag.art.uniroma2.it/demo-software/kelp/
[maven-site]: http://maven.apache.org "Apache Maven"
[github]: https://github.com/SAG-KeLP

