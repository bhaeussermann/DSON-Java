package org.dogeon.dson.makesense;

public interface ThingVisitor
{
    public void visitValue(Object value);
    public void visitMember(String name);
    public void visitSuchComposite();
    public void visitCompositeWow();
    public void visitSuchList();
    public void visitListWow();
}
