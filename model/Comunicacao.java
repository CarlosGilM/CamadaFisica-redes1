/* ***************************************************************
* Autor............: Carlos Gil Martins da Silva
* Matricula........: 202110261
* Inicio...........: 31/08/2023
* Ultima alteracao.: 05/09/2023
* Nome.............: Comunicacao
* Funcao...........: Recebe os Bits manipulados e passa para
o fluxo B que será passado para o Receptor logo em seguida, tambem
faz o controle e animação da onda de sinais por meio da utilizacao de
threads
****************************************************************/

package model;
import control.controllerPrincipal;

public class Comunicacao {

  public void MeioDeComunicacao(int[] fluxoBrutoDeBits, controllerPrincipal cT) {
    Receptor rT = new Receptor(); // Instancia do Receptor
    int[] fluxoBrutoDeBitsPontoA;
    int[] fluxoBrutoDeBitsPontoB = new int[cT.setTamanhoArray(cT.getCodificacao())]; // Seta o tamanho do Array
    fluxoBrutoDeBitsPontoA = fluxoBrutoDeBits;
    new Thread(() -> {
      int deslocBit = 0;

      switch(cT.getCodificacao()){
        case 0: // SETA A FORMA DE ONDA BINARIA
        for (int i=0; i < fluxoBrutoDeBitsPontoA.length; i++){
          fluxoBrutoDeBitsPontoB[i] = fluxoBrutoDeBitsPontoA[i]; //BITS SENDO TRANSFERIDOS
        }
        //Setando a Forma de Onda
        for (int i = 0; i < cT.getNumCaracteres(); i++) { // For ate o tamanho da Mensagem
          for (int j = 0; j < 8; j++) { // For para cada Bit
            int mascara = 1 << deslocBit; // Mascara com bit 1 na Posicao deslocBit
            int Bit = (fluxoBrutoDeBitsPontoA[i / 4] & mascara) >> deslocBit; // Pega o Bit na posicao da Mascara&Quadro
              if (Bit == -1) {
                Bit = Bit * -1; // Verificacao do Bit negativo (Se o Binario começa com o Digito 1)
              }                
            cT.adiantaSignal(cT.getCodificacao()); // Adianta os Sinais Mostrados na Tela
            cT.updateSignalBinario(Bit, deslocBit); // Seta o bit pego do Array no Sinal da Tela
            deslocBit++;
            try {
              Thread.sleep(cT.getSliderWave()); // Sleep com o valor do Slider
            } catch (InterruptedException e) {
            }
          }
        }

        break;
        default: // SETA A FORMA DE ONDA MANCHESTER DIFERENCIAL OU NORMAL       
         for (int i=0; i < fluxoBrutoDeBitsPontoA.length; i++){ 
          fluxoBrutoDeBitsPontoB[i] = fluxoBrutoDeBitsPontoA[i]; // Bits sendo Transferidos
        }
        // SETANDO A FORMA DE ONDA 
        for (int i = 0; i < cT.getNumCaracteres(); i++) { // For ate o tamanho da Mensagem
          if (deslocBit == 32) {
            deslocBit = 0;
          }
          for (int t = 0; t < 8; t++) {
            StringBuilder parBit = new StringBuilder();
            for (int j = 0; j < 2; j++) { // For para cada Bit
              int mascara = 1 << deslocBit; // Mascara com bit 1 na Posicao deslocBit
              int Bit = (fluxoBrutoDeBitsPontoA[i / 2] & mascara) >> deslocBit; // Pega o Bit na posicao da Mascara&Quadro
              if (Bit == -1) {
                Bit = Bit * -1; // Verificacao do Bit negativo (Se o Binario começa com o Digito 1)
              }
              parBit.insert(0, Bit); // Insere o Bit no parBit que será um par de Bits
              deslocBit++;
            }
            cT.adiantaSignal(cT.getCodificacao()); // Adianta os Sinais Mostrados na Tela
            cT.updateSignalManchester(parBit.toString()); // Seta o bit pego do Array no Sinal da Tela
            try {
              Thread.sleep(cT.getSliderWave()); // Sleep com o valor do Slider
            } catch (InterruptedException e) {
            }
          }
        } // Fim for msg
        break;
      } // Fim Switch
      rT.CamadaFisicaReceptora(fluxoBrutoDeBitsPontoB, cT); // Chama o Receptor
    }).start(); // Fim thread
  } // Fim meio de Comunic
  }// fim do metodo MeioDeTransmissao