package com.angiefans.temp.entity;

public class Person implements Cloneable {

    public String name = "aaa";

    static {
        System.out.println("Person 静态代码块");
    }

    public static class Inner {
        public void aaa() {
            System.out.println("aaa()");
        }
    }

    public int test(int num) {
        System.out.println("test()");
        return num * 6;
    }

    public int xmove(int x) {
//		if (x == 4) {
//			return x;
//		}
        System.out.println("xmove()");
//		xmove(++x);
        return 100;
    }

    public static int ymove(int x) {
        System.out.println("ymove()");
        return 200;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Person other = (Person) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
