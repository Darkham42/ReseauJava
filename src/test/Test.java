package test;

import java.io.IOException;

import serverSide.Server;

public class Test {
	public static void main(String[] args) throws InterruptedException{
		try {
			Server server = new Server(2015);//instanciation d'un serveur sur le port 2015
			server.serverOn();//démarrage du serveur;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
