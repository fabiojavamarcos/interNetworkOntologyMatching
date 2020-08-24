# interNetworkOntologyMatching
A preprocessing tool that checks for the same entities in both networks and avoids compare them. Implementations of the SubInterNM approach defined in: https://www.researchgate.net/publication/328809054_A_Proposal_for_Optimizing_Internetwork_Matching_of_Ontologies

# SubInterNM
 InterNetwork Ontology Matching Tool

Example of use:
Run the main.Main.java with the following arguments:
/Users/home/Documents/dev/conferenceOAEI/
2
2
0
sigkdd.owl 
confOf.owl
conference.owl
confOf.owl
/Users/home/Dropbox/workdir/experiment
N
N

--------------------------------------
Where :

Parameter # - description

0 - dir where owl are stores
1 - number of ontologies in network 1
2 - number of ontologies in network 2
3 - number of intranetwork alignments
4,5,6,7 - ontology files in owl format
8 - output dir
9 - debug (Y/N)
10 - messages (Y/N)

If the number of network elements is bigger parameters 8,9 and 10 will be shifted forward!

Feel free to get in touch:
fabiojavamarcos@gmail.com
