package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

public class ChooseDataSetListener implements ActionListener{
	private File taxonomy;
	private File challenge;
	private File services;
	private File serviceLevelAgreement;
	
	public File getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(File taxonomy) {
		this.taxonomy = taxonomy;
	}

	public File getChallenge() {
		return challenge;
	}

	public void setChallenge(File challenge) {
		this.challenge = challenge;
	}

	public File getServices() {
		return services;
	}

	public void setServices(File services) {
		this.services = services;
	}

	public File getServiceLevelAgreement() {
		return serviceLevelAgreement;
	}

	public void setServiceLevelAgreement(File serviceLevelAgreement) {
		this.serviceLevelAgreement = serviceLevelAgreement;
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		JFileChooser chooser = new JFileChooser();

		// DIRECTORIES_ONLY就是只选目录
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int r = chooser.showOpenDialog(null);
		System.out.println(chooser.getSelectedFile().getPath());
		taxonomy = new File(chooser.getSelectedFile().getPath()+"\\"+"Taxonomy.owl");
		challenge = new File(chooser.getSelectedFile().getPath()+"\\"+"Challenge.wsdl");
		services = new File(chooser.getSelectedFile().getPath()+"\\"+"services.xml");
		serviceLevelAgreement = new File(chooser.getSelectedFile().getPath()+"\\"+"Servicelevelagreements.wsla");
//		if(taxonomy.exists()){
//			System.out.println("taxonomy");
//		}
//		if(challenge.exists()){
//			System.out.println("challenge.wsdl");
//		}
//		if(services.exists()){
//			System.out.println("services.xml");
//		}
//		if(serviceLevelAgreement.exists()){
//			System.out.println("serviceLevelAgreement");
//		}
			
	}

}
