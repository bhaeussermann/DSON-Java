package org.dogeon.dson.util;

public interface ThingVisitor
{
    public void visitEmptySad();
    public void visitValue(Object value);
    public void visitMember(String name, boolean isFirst);
    public void visitSuchComposite();
    public void visitCompositeWow();
    public void visitSuchList();
    public void visitListWow();
}
