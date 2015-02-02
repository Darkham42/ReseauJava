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
 * boolean marche: symbolise l'etat du client, connect�, ou d�connect�, s'il est d�connect�, le thread s'arrete.
 * nom: il s'agit de l'identifiant du client, une chaine de caract�re.
 * out et in : permettent de g�r� le flux de donn�es du client connect�, afficher des donn�es ou attendre l'insertion de donn�es.
 * server : instance du server sur lequel le client est connect�.
 */
public class Client implements Runnable{
	private boolean marche;
	private String nom;
	private PrintWriter out;
	private BufferedReader in;
	private Server server;
	
	/*
	 * constructeur:
	 * 	server: l'instance du server sur lequel le client est connect�,
	 * 	socket: le socket du client.
	 * Le constructeur initialise les attributs in et out qui permettent de comuniquer avec le client.
	 */
	public Client(Server server, Socket socket) throws IOException{
		this.out =new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.server = server;
	}
	
	/*
	 * c'est cette methode que le client ex�cute lorsque le thread d�marre.
	 * Tant que l'utilisateur n'a pas entr�e les donn�es : ":close" (pour se d�connecter),
	 * il pourra �crire et recevoir des informations avec les autres clients.
	 * 
	 */
	@Override
	public void run() {
		Scanner sc = new Scanner(this.in);
		this.out.print("Pseudo :");//demande � l'utilisateur son pseudo
		this.out.flush();
		this.nom = sc.nextLine();
		server.ajoute(this);//ajoute ce client � la liste des clients connect�
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
	 * ex�cute la commande "string"
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
				server.remove(this);//on retire ce client de la liste des clients connect�s.
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
