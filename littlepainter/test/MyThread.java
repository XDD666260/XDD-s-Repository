package com.example.littlepainter.test;

public class MyThread {
    private static class InstanceSyn implements Runnable{
        private MyThread myThread;

        public InstanceSyn(MyThread myThread){
            this.myThread=myThread;
        }
        @Override
        public void run() {
            System.out.println("TestInstance is running...."+myThread);
            myThread.instance();
        }
    }
    private static class Instance2Syn implements Runnable{
        private MyThread myThread;

        public Instance2Syn(MyThread myThread){
            this.myThread=myThread;
        }
        @Override
        public void run() {
            System.out.println("TestInstance2 is running...."+myThread);
            myThread.instance2();
        }
    }
    private synchronized void instance(){
        System.out.println("synInstance is going...");
        System.out.println("synInstance ended");
    }
    private synchronized void instance2(){
        System.out.println("synInstance2 is going...");
        System.out.println("synInstance2 ended");
    }
    public static void main(String [] args){
        MyThread instance1=new MyThread();
        Thread t1=new Thread(new InstanceSyn(instance1));
        MyThread instance2=new MyThread();
        Thread t2=new Thread(new Instance2Syn(instance2));
        t1.start();
        t2.start();
    }
}

