DSON-Java
=========

A complete, zero-dependency Java implementation of the Doge Serialized Object Notation ([dogeon.org](http://dogeon.org/)).

Supports both serialization and parsing. Properly converts all numbers from and to octal base.

Some of the code uses names so as to make the code more understandable by Shiba Inu dogs.

### Usage

```java
public static class DogeExample
{
    private String many;
    private List<String> such = new ArrayList<String>();
    
    public String getMany()
    {
        return many;
    }
    
    public void setMany(String newMany)
    {
        many = newMany;
    }
    
    public List<String> getSuch()
    {
        return such;
    }
}
```

#### Shibe.speak()

```java
DogeExample d = new DogeExample();
d.setMany("wow");
d.getSuch().add("foo");
d.getSuch().add("doge");
d.getSuch().add("inu");

System.out.println(Shibe.speak(d));
```

```
such "many" is "wow","such" is so "foo" also "doge" and "inu" many wow
```

=====

```java
DogeList list = new DogeList();
list.add("foo");
list.add("doge");
list.add("inu");

DogeThing thing = new DogeThing();
thing.put("such", list);
thing.put("many", "wow");

System.out.println(Shibe.speak(thing));
```

```
such "many" is "wow","such" is so "foo" also "doge" and "inu" many wow
```

#### Shibe.makeSense()

```java
DogeThing d = (DogeThing)Shibe.makeSense("such \"many\" is \"wow\", \"such\" is so \"foo\" and \"doge\" and \"inu\" many wow");

System.out.println(d.get("many"));
for (Object next : (DogeList)d.get("such"))
    System.out.println(next);
```

```
wow
foo
doge
inu
```

=====

```java
DogeExample d = (DogeExample)Shibe.makeSense("such \"many\" is \"wow\", \"such\" is so \"foo\" and \"doge\" and \"inu\" many wow", DogeExample.class);

System.out.println(d.getMany());
for (String next : d.getSuch())
    System.out.println(next);
```

```
wow
foo
doge
inu
```
