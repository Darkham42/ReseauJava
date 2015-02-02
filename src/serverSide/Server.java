package serverSide;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import clientSide.*;
/*
 * ServerSocket : il s'agit du socket serveur du serveur.
 * ArrayList<CLient> : la liste des clients qui se connectent aux serveur.
 * boolean marche : un booléen qui permet de démarrer ou d'éteindre le serveur (en théorie).
 */
public class Server {
	private ServerSocket serverSocket;
	private ArrayList<Client> clients;
	private boolean marche;
	
	/*
	 * constructeur:
	 * port: entier qui correspond au port TCP que le serveur va "écouté"
	 * le constructeur initialise la liste des clients, le serverSocket et place le serveur à l'etat "éteint"
	 */
	public Server(int port) throws IOException{
		this.clients = new ArrayList<Client>();
		this.serverSocket = new ServerSocket(port);
		this.marche = false;
	}
	/*
	 * met en marche le serveur.
	 */
	public void serverOn() throws IOException{
		this.marche = true;
		serverRun();
	}
	/*
	 * éteint le serveur.
	 */
	public void serverOff(){
		this.marche = false;
	}
	
	/*
	 * retire un client de la liste des clients,
	 * notifie tout les clients de la déconnexion d'un utilisateur.
	 */
	public void remove(Client e){
		String nom = e.getNom();
		for(int i=0 ; i<this.clients.size() ; i++){
			if(e.equals(this.clients.get(i))){
				this.clients.remove(i);
				this.broadcoast("Deconnexion de: " + nom);
				break;
			}
		}
	}
	/*
	 * ajoute un client à la liste des clients,
	 * notifie tout les clients de connexion d'un utilisateur.
	 */
	public void ajoute(Client e){
		this.clients.add(e);
		this.broadcoast("Connexion de : " + e.getNom());
	}
	/*
	 * c'est ici que le serveur tourne, il est en attente d'un nouveau client tant que le serveur marche.
	 */
	private void serverRun() throws IOException{
		while(this.marche){
			//création d'un thread lors de la connexion d'un client.
			Thread t1 = new Thread(new Client(this, serverSocket.accept()));
			t1.start();
		}
		broadcoast("[Server]Fermeture du serveur, vous allez etre deconnecte");
		for(int i=0 ; i<this.clients.size() ; i++){
			this.clients.get(i).commande(":close");
		}
	}
	/*
	 * message: une chaine de caractère qui correspond au message à envoyer aux réseaux,
	 * le réseaux est constitué de tout les utilisateurs connectés aux serveur.
	 */
	public void broadcoast(String message){
		for(int i=0 ; i<this.clients.size() ; i++){
			this.clients.get(i).display(message);
		}
	}
	/*
	 * permet d'informer un client sur l'état du réseaux (le nombre d'utilisateurs connectés).
	 */
	public String info(){
		String str = "=>Utilisateurs connectes :";
		for(int i=0 ; i<this.clients.size() ; i++){
			str = str + "\n\r" + this.clients.get(i).getNom();
		}
		return str;
	}
}