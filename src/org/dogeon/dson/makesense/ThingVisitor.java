package org.dogeon.dson.makesense;

public interface ThingVisitor
{
    public void visitValue(Object value);
    public boolean visitMember(String name);
    public void visitSuchComposite() throws MakeSenseException;
    public void visitCompositeWow();
    public void visitSuchList() throws MakeSenseException;
    public void visitListWow();
}
