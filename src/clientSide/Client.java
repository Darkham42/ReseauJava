package clientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import serverSide.*;
/*
 * boolean marche: symbolise l'etat du client, connecté, ou déconnecté, s'il est déconnecté, le thread s'arrete.
 * nom: il s'agit de l'identifiant du client, une chaine de caractère.
 * out et in : permettent de géré le flux de données du client connecté, afficher des données ou attendre l'insertion de données.
 * server : instance du server sur lequel le client est connecté.
 */
public class Client implements Runnable{
	private boolean marche;
	private String nom;
	private PrintWriter out;
	private BufferedReader in;
	private Server server;
	
	/*
	 * constructeur:
	 * 	server: l'instance du server sur lequel le client est connecté,
	 * 	socket: le socket du client.
	 * Le constructeur initialise les attributs in et out qui permettent de comuniquer avec le client.
	 */
	public Client(Server server, Socket socket) throws IOException{
		this.out =new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.server = server;
	}
	
	/*
	 * c'est cette methode que le client exécute lorsque le thread démarre.
	 * Tant que l'utilisateur n'a pas entrée les données : ":close" (pour se déconnecter),
	 * il pourra écrire et recevoir des informations avec les autres clients.
	 * 
	 */
	@Override
	public void run() {
		Scanner sc = new Scanner(this.in);
		this.out.print("Pseudo :");//demande à l'utilisateur son pseudo
		this.out.flush();
		this.nom = sc.nextLine();
		server.ajoute(this);//ajoute ce client à la liste des clients connecté
		this.marche = true;
		while(this.marche){
			commande(sc.nextLine());
		}
		//fermeture du client.
		this.out.close();//on ferme les flux.
		try {
			sc.close();
			this.in.close();//on ferme les flux.
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * permet d'afficher le "message" sur le terminal du client distant.
	 */
	public void display(String message){
		this.out.print(message + "\n\r");
		this.out.flush();
		prompt();
	}
	
	/*
	 * il s'agit du prompt du client.
	 */
	public void prompt(){
		this.out.print("$" + this.nom + ">");
		this.out.flush();
	}
	
	/*
	 * retourne le nom du client.
	 */
	public String getNom(){
		return this.nom;
	}
	/*
	 * exécute la commande "string"
	 */
	public void commande(String string){
		String str = "";
		if(string.indexOf(' ') != -1){
			str = string.substring(0, string.indexOf(' '));
		}
		else{
			str = string;
		}
		switch(str){
			case ":help":
				display(":close => deconnexion du serveur\n\r" +
						":info => affiche les utilisateurs en ligne\n\r" +
						":send <message> => envoi le <message> a tout le monde");
			break;
			case ":close":
				this.marche = false;
				server.remove(this);//on retire ce client de la liste des clients connectés.
			break;
			case ":info":
				display(this.server.info());
			break;
			case ":send":
				String message = string.substring(string.indexOf(' ')+1);
				this.server.broadcoast(this.nom + ": " + message);
			break;
			default :
				display("Commande non reconnu, :help pour afficher l'aide");
		}
	}
}
