package it.poliba.sisinflab.simlib.neighborhood.pathbaseditem;

import it.poliba.sisinflab.simlib.datamodel.Node;
import java.util.HashSet;
import java.util.LinkedList;


/**
 * Created by Corrado on 10/04/2016.
 */
public class EntityPath{

    //path percorribile da i
    private LinkedList<Node> entitypathList;
    //sub-paths dell'entityPath percorribile dall'item i 
    protected LinkedList<EntityPath> entitysubpaths = new LinkedList<>();

    public EntityPath(){
        entitypathList = new LinkedList<>();
    }
  
    public EntityPath(LinkedList<Node> entitypathList,LinkedList<EntityPath> entitysubpaths){
        this.entitypathList = new LinkedList<>(entitypathList);
        this.entitysubpaths = new LinkedList<>(entitysubpaths);
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof EntityPath){
            EntityPath p = (EntityPath) o;
            return entitypathList.equals(p.getPathList());
        }
        return false;
    }

    @Override
    public int hashCode(){
        int h = 0;
        for(Node p : entitypathList)
            h += p.hashCode();

        return h;
    }

    public LinkedList<Node> getPathList() {
        return entitypathList;
    }

    public void setPathList(LinkedList<Node> entitypathList) {
        this.entitypathList = entitypathList;
    }
    
    
    public LinkedList<EntityPath> getSubPathList() {
        return entitysubpaths;
    }

    public void setSubPathList(LinkedList<EntityPath> entitysubpaths) {
        this.entitysubpaths = entitysubpaths;
    }

    public void removeFirst() {
        entitypathList.removeFirst();
    }
    
    
    public LinkedList<EntityPath> calculateReachableSubPaths(){
        //calcolo i subpaths partendo dall'insieme Pi
        //for(EntityPath e : entitypaths)
        //{    
            entitysubpaths.clear();
            LinkedList<EntityPath> listsubpath = new LinkedList<>();
            LinkedList<Node> nodes = new LinkedList<>();
            EntityPath w = new EntityPath();
            nodes.addAll(this.getPathList());
            w.setPathList(nodes);
            do{
                w.removeFirst();
                LinkedList<Node> nodes2 = new LinkedList<>();
                EntityPath w2 = new EntityPath();
                nodes2.addAll(w.getPathList());
                w2.setPathList(nodes2);
                if(w2.getPathList().size()<this.getPathList().size())
                {
                    listsubpath.add(w2);
                }
            }while(w.getPathList().size()>1);
            //System.out.println();
            entitysubpaths.addAll(listsubpath);
        //}
        return listsubpath;
    }
    

    
    
    
}
