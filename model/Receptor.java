/* ***************************************************************
* Autor............: Carlos Gil Martins da Silva
* Matricula........: 202110261
* Inicio...........: 31/08/2023
* Ultima alteracao.: 05/09/2023
* Nome.............: Receptor
* Funcao...........: Recebe os Bits manipulados e
descriptografa, fazendo o processo inverso do transmissor
até chegar na mensagem enviada pelo usuario
****************************************************************/
package model;
import control.controllerPrincipal;

public class Receptor {

	void CamadaFisicaReceptora(int quadro[], controllerPrincipal cT) {
		int tipoDeDecodificacao = cT.getCodificacao(); // alterar de acordo o teste
		int[] fluxoBrutoDeBits = new int[cT.setTamanhoArray(cT.getCodificacao())]; // ATENcaO: trabalhar com BITS!!!
		switch (tipoDeDecodificacao) {
			case 0: // codificao binaria
				fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoBinaria(quadro, cT);
				break;
			case 1: // codificacao manchester
				fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoManchester(quadro, cT);
				break;
			case 2: // codificacao manchester diferencial
				fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(quadro, cT);
				break;
		}// fim do switch/case
		CamadaDeAplicacaoReceptora(fluxoBrutoDeBits, cT);
	}// fim do metodo CamadaFisicaTransmissora

	public int[] CamadaFisicaReceptoraDecodificacaoBinaria(int quadro[], controllerPrincipal cT) {
		// Ja está em binario, não havendo necessidade de decodificar o Arrray
		return quadro;
	}// fim do metodo CamadaFisicaReceptoraDecodificacaoBinaria

	public int[] CamadaFisicaReceptoraDecodificacaoManchester(int quadro[], controllerPrincipal cT) {
		int[] quadroDescrip = new int[cT.setTamanhoArray(cT.getCodificacao())];
		int deslocBit = 0; // Variavel define qual bit vai deslocar para manipulação
		int deslocBitDescript = 0; // Variavel define qual bit vai deslocar para manipulação 

		for (int i = 0; i < cT.getNumCaracteres(); i++) { // For ate o tamanho da Mensagem
			if (deslocBit == 32) { // verifica o ultimo bit de cada posicao do array
				deslocBit = 0;
			}
			for (int t = 0; t < 8; t++) { // for para cada Caractere
				StringBuilder parBit = new StringBuilder();
				for (int j = 0; j < 2; j++) { // For para cada Par de Bit
					int mascara = 1 << deslocBit; // Mascara com bit 1 na Posicao deslocBit
					int Bit = (quadro[i / 2] & mascara) >> deslocBit; // Pega o Bit na posicao da Mascara&Quadro na posicao	desloc bit
					if (Bit == -1) {
						Bit = Bit * -1;
					}
					parBit.insert(0, Bit); // Insere o Bit no parBit que será um par de Bits
					deslocBit++;
				}
				if (parBit.toString().equals("10")) { // Verifica se o par de Bits eh 10
					quadroDescrip[i / 4] = quadroDescrip[i / 4] | (1 << (deslocBitDescript)); // Insere o Bit 1 na posicao deslocBit 																																							
				}
				deslocBitDescript++;
			}
		} // Fim for msg
		return quadroDescrip;
	}// fim do metodo CamadaFisicaReceptoraDecodificacaoManchester

	public int[] CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(int quadro[], controllerPrincipal cT) {
		int[] quadroDescrip = new int[cT.setTamanhoArray(cT.getCodificacao())];
		int deslocBit =0; // Variavel define qual bit vai deslocar para manipulação
		int deslocBitDescript = 0; // Variavel define qual bit vai deslocar para manipulação
		
		for (int i = 0; i < cT.getNumCaracteres(); i++) { // For ate o tamanho da Mensagem
			String bitComparation = "10"; // Bit de comparacao inicial
			boolean verifyComparation = true; // varival de controle para comparacao

			if(i % 2== 0){ // alterna o valor do deslocamento entre 15 ( ultimo bit da primeiro caractere da posicao)
				deslocBit = 15;
			}
			else{ // alterna o valor do deslocamento entre 31 ( ultimo bit da segunda caractere da posicao)
				deslocBit = 31;
			}
			for (int t = 0; t < 8; t++) { // for para cada caractere
				StringBuilder parBit = new StringBuilder();
				for (int j = 0; j < 2; j++) { // For para cada par de Bits
					int mascara = 1 << deslocBit; // Mascara com bit 1 na Posicao deslocBit
					int Bit = (quadro[i / 2] & mascara) >> deslocBit; // Pega o Bit na posicao da Mascara&Quadro na posicao eslocBit																
					if (Bit == -1) {
						Bit = Bit * -1;
					}
					parBit.append(Bit); // Insere o Bit no parBit que sera um par de Bits
					deslocBit--;
				}

				if(parBit.toString().equals(bitComparation)){ // Verifica se deve ser inserido ou nao o Bit 1
					if(verifyComparation == true){
						quadroDescrip[i / 4] = quadroDescrip[i / 4] | (1 << (deslocBitDescript)); // insere o bit 1
						verifyComparation = false;
						bitComparation = parBit.toString();	
					}
				}
				else{
					if(verifyComparation == false){
						quadroDescrip[i / 4] = quadroDescrip[i / 4] | (1 << (deslocBitDescript)); // insere o bit 1
						bitComparation = parBit.toString();	
					}
				}
				deslocBitDescript++;
			}
		} // Fim for msg
		return quadroDescrip;
	}// fim do CamadaFisicaReceptoraDecodificacaoManchesterDiferencial

	void CamadaDeAplicacaoReceptora(int quadro[], controllerPrincipal cT) {
		int deslocBit = 0;
		StringBuilder Mensagem = new StringBuilder();
		for (int i = 0; i < cT.getNumCaracteres(); i++) { // For ate o tamanho da Mensagem
			StringBuilder Char = new StringBuilder();
			for (int j = 0; j < 8; j++) { // For para cada Bit
				int mascara = 1 << deslocBit; // Mascara com bit 1 na Posicao deslocBit
				int Bit = (quadro[i / 4] & mascara) >> deslocBit; // Pega o Bit na posicao da Mascara&Quadro
				if (Bit == -1) {
					Bit = Bit * -1;
				}
				Char.insert(0, Bit); // insere o bit no caractere
				deslocBit++;
			}
			if(cT.getCodificacao() == 2){ // Verifica o tipo de codificacao, caso seja Diferencial
				Char.reverse(); // Reverte o binario, pois foi decodificado de frente para tras
			}

			int aux = Integer.parseInt(Char.toString(), 2); // converte o binario em inteiro
			Mensagem.append((char) aux); // converte o inteiro em char
		}
		AplicacaoReceptora(Mensagem.toString(), cT);
		System.out.println("Mensagem Descriptografada: " + Mensagem.toString());
	}// fim do metodo CamadaDeAplicacaoReceptora

	void AplicacaoReceptora(String mensagem, controllerPrincipal cT) {
		cT.disableButtons();
		cT.setFinalText(mensagem);
	}// fim do metodo AplicacaoReceptora
}
