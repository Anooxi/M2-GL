class ListeTableau implements IMegaListeGet {
    private void secretLT(){}
    public static void staticLT() {}
    int nbLT;
}
class ListeChainee implements IMegaListeGet,IMegaListePeekPool {
    private void secretLC(){}
}
class QueueDoubleEntree implements IMegaListePeekPool, IMegaListe{
    private void secretQDE(){}
}
class QueueAvecPriorite implements IMegaListePeekPool , IMegaListe{
    public Object comparator() {return null;}
    private void secretQAP(){}
}
interface IMegaListe{
    default boolean add(Object o){return true;}
    default boolean isEmpty(){return true;}
}
interface IMegaListeGet extends IMegaListe{
    default Object get(int i) {return null;}
}
interface IMegaListePeekPool{
    default Object peek() {return null;}
    default Object poll() {return null;}
}
