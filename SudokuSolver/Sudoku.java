package SudokuSolver;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;
 
public class Sudoku
{
	int [] puzzle = {
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0};
	
	int [] rank = {0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0};
	
	int count = 0;
    
	public Sudoku(int[] puzzle) {
		for(int i=0;i<81;i++) {
			this.puzzle[i] = puzzle[i];
		}
	}
	
	public int SolveNew() {
		GetRanking();
		count++;
		return SolveSudokuRecNew(puzzle);
	}
	
	public int SolveOld() {
		return SolveSudokuRecOld(puzzle);
	}
	
	public int SolveSudokuRecOld(int[] puzzle){
		int i,j;
		int[] findfirst = this.findfirstold();
		if(findfirst[0]==-1 && findfirst[1]==-1) {return 0;}
		i = findfirst[0];
		j = findfirst[1];
		for(int val=1;val<=9;val++) {
			if(this.isValid(i, j, val)==0)
			{
	            puzzle[i*9+j] = val;
	            if (SolveSudokuRecOld(puzzle)==0) {
	                return 0;}
	            else {puzzle[i*9+j]=0;
	            }
	        }
		}
		return -1;
	}
	
	public int SolveSudokuRecNew(int[] puzzle){
		int i,j;
		//ICI
		int[] findfirst = this.findfirstnew();
		if(findfirst[0]==-1 && findfirst[1]==-1) {return 0;}
		i = findfirst[0];
		j = findfirst[1];
		for(int val=1;val<=9;val++) {
			if(this.isValid(i, j, val)==0)
			{
	            puzzle[i*9+j] = val;
	            if (SolveSudokuRecNew(puzzle)==0) {
	                return 0;} 
	            else {puzzle[i*9+j]=0;
	            count--;}
	        }
		}
		return -1;
	}
	
	
	
	//On classe les cases de celles ayant le moins de possibilités à celles en ayant le plus.
	//On place ce classement dans une liste.
	public void GetRanking() {
		int [] rank_temp = {0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0};
		int [] numberPossibilities = {0,0,0,0,0,0,0,0,0};
		int compteur = 1;
		int [] posi = {0,0};
		int temp = 0;
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				if (this.puzzle[i*9+j]==0) {
					temp = NumberOfPossibilities(i,j);
					rank_temp[i*9+j]=temp;
					temp--;
					numberPossibilities[temp]=numberPossibilities[temp]+1;
					}
			}
		}
		
		boolean done = false;
		int number = 1;
		while(!done) {
			posi = findFirstPossi(number,rank_temp);
			if(posi[0]==-1 && posi[1]==-1) {
				if(number==9) {done = true;}
				else {number++;}
			}
			else {
				rank_temp[posi[0]*9+posi[1]]=0;
				rank[posi[0]*9+posi[1]]=compteur;
				compteur++;
			}	
		}
		
	}
	
	
	/*
	 * @pre : number est le nombre de solution possible, compris entre 1 et 9
	 * rank_temp est le tableau avec a la place des chiffres, les solutions possibles
	 * @post : renvoit un int[2] avec int[0] = x et int[1] = y
	 * 
	 */
	public int[] findFirstPossi(int number,int[] rank_temp) {
		int[] retour = {-1,-1};
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				if(rank_temp[i*9+j]==number) {
					retour[0]=i;retour[1]=j;
					return retour;
				}
			}
		}
		return retour;
	}
	
	public int NumberOfPossibilities(int x, int y) {
		int number = 0;
		for(int i=1;i<=9;i++) {
			 if(isValid(x,y,i)==0) {
				 number++;
			 }
		}
		return number;
	}
	
	
   /*
    * renvoi la première case vide du sudoku celon le classqement rank
    * @pre: reçoit un sudoku en paramètre
    * @post: renvoit les coordonnées {x,y} du premier element vide
    * si sudoku résolu, renvoi {-1,-1}
    */
   public int[] findfirstnew(){
       int[] retour = {-1,-1};
       for(int i=0;i<81;i++){
            if(this.rank[i]==this.count){
            	retour[0] = i/9;
                retour[1] = i%9;
                count++;
                return retour;
            }        	
       }
       return retour;
   }
   /*
    * renvoi la première case vide du sudoku
    * @pre: reçoit un sudoku en paramètre
    * @post: renvoit les coordonnées {x,y} du premier element vide
    * si sudoku résolu, renvoi {-1,-1}
    */
   public int[] findfirstold(){
       int[] retour = {-1,-1};
       for(int i=0;i<81;i++){
           if(this.puzzle[i]==this.count){
               retour[0] = i/9;
               retour[1] = i%9;
           }
       }
       return retour;
   }
   
  
   
   /*
    * dit si la solution d'une case est valide
    * @pre : prend en paramètre des coordonnées et une solution possible
    * x et y sont compris entre 0 et 8, a est compris entre 1 et 9
    * @post : renvoi 0 si valide
    * renvoi 1 si seule la section était valide
    * renvoi 2 si seule la ligne était valide
    * renvoi -1 si rien de valide
    * 
    * 0 0 0
    * 0 0 0
    * 0 1 0
    * 1 est en (2,3) x=2 y=3
    */
   public int isValid(int x, int y, int a){
       int secValid = isSecValid(x, y, a);
       int lineValid = isLineValid(x, y, a);
       if(secValid==0 && lineValid==0){return 0;}
       else{
           if (secValid==0){return 1;}
           else if (lineValid==0){return 2;}
           else return -1;
        }
    }
    
   /*
    * dit si la section est valide
    * @pre : prend un puzzle, les coordonnées d'un point et une solution
    * @post : renvoit 0 si la section du point est valide
    * renvoit -1 sinon
    */
    public int isSecValid(int x, int y, int a){
        int sec = secNum(x,y);
        int[] secnum = retSec(sec);
        for(int i=0;i<9;i++){
            if(a==secnum[i]){
                return -1;
            }
        }
        return 0;
    }

    /*
     * renvoi un tableau avec les éléments de la section
     * @pre : prend un puzzle valide en paramètre, et un numéro de section entre 1 et 9
     * @post : renvoit un tableau de 9 éléments comprenent tout les éléments de la section
     * 
     * 
     * 1,1 2,1 3,1  4,1 5,1 6,1  7,1 8,1 9,1
     * 1,2 2,2 3,2  4,2 5,2 6,2  7,2 8,2 9,2 
     * 1,3 2,3 3,3  4,3 5,3 6,3  7,3 8,3 9,3
     * 
     * 1,4 2,4 3,4  4,4 5,4 6,4  7,4 8,4 9,4
     * 1,5 2,5 3,5  4,5 5,5 6,5  7,5 8,5 9,5
     * 1,6 2,6 3,6  4,6 5,6 6,6  7,6 8,6 9,6
     * 
     * 1,7 2,7 3,7  4,7 5,7 6,7  7,7 8,7 9,7
     * 1,8 2,8 3,8  4,8 5,8 6,8  7,8 8,8 9,8
     * 1,9 2,9 3,9  4,9 5,9 6,9  7,9 8,9 9,9
     * 
     */
    public int[] retSec(int section){
        int x1,x2,x3,y1,y2,y3;
        x1=0;x2=0;x3=0;y1=0;y2=0;y3=0;
        if(section== 1 || section == 4 || section == 7){x1=0; x2=1; x3=2;}
        else if(section== 2 || section == 5 || section == 8){x1=3; x2=4; x3=5;}
        else if(section== 3 || section == 6 || section == 9){x1=6; x2=7; x3=8;}
         
        if(section== 1 || section == 2 || section == 3){y1=0; y2=1; y3=2;}
        else if(section== 4 || section == 5 || section == 6){y1=3; y2=4; y3=5;}
        else if(section== 7 || section == 8 || section == 9){y1=6; y2=7; y3=8;}
        int[] retour = {this.puzzle[x1*9+y1],this.puzzle[x2*9+y1],this.puzzle[x3*9+y1],this.puzzle[x1*9+y2],this.puzzle[x2*9+y2],
        		this.puzzle[x3*9+y2],this.puzzle[x1*9+y3],this.puzzle[x2*9+y3],this.puzzle[x3*9+y3]};
        return retour;
    }
    
    /*
     * dis si les lignes sont valide pour une certaine solution
     * @pre : prend en paramètre un puzzle valide, des coor et une solution
     * @post : renvoi 0 si la solution est valide pour les lignes et -1 sinon
     */
    public int isLineValid(int x, int y, int a){
        if(isXLineValid(x, a)==0 && isYLineValid(y, a)==0){
            return 0;
        }
        else return -1;
    }
    
    /*
     * dis si la ligne X est valide
     * @pre : prend en paramètre un puzzle valide, des coor et une solution
     * @post : renvoi 0 si la sol est valide pour la ligne et -1 sinon
     */
    public int isXLineValid(int x, int a){
        for(int i=0;i<9;i++){
            if(a==this.puzzle[x*9+i]){
                return -1;
            }
        }
        return 0;
    }
    
    /*
     * dis si la ligne Y est valide
     * @pre : prend en paramètre un puzzle valide, des coor et une solution
     * @post : renvoi 0 si la sol est valide pour la ligne et -1 sinon
     */
    public int isYLineValid(int y, int a){
        for(int i=0;i<9;i++){
            if(a==this.puzzle[i*9+y]){
            return -1;}
        }
        return 0;
    }
    
    /* renvoi le numéro de la section
     * @pre : prend des coordonnées en paramètre
     * @post : renvoi le numéro de la section celon le shéma suivny
     * 
     * 1 2 3
     * 4 5 6
     * 7 8 9
     * 
     * renvoi -1 si il y a un soucis
     */
    public int secNum(int x, int y){
        int X;
        int Y;
        if(x<3){X=1;}
       else if(x<6){X=2;}
       else{X=3;}
       if(y<3){Y=1;}
       else if(y<6){Y=2;}
       else{Y=3;}
       if(X==1){
           if(Y==1){return 1;}
           if(Y==2){return 4;}
           if(Y==3){return 7;}
        }
        else if(X==2){
           if(Y==1){return 2;}
           if(Y==2){return 5;}
           if(Y==3){return 8;}
        }
        else{
           if(Y==1){return 3;}
           if(Y==2){return 6;}
           if(Y==3){return 9;}
        }
        return -1;
    }
 
    /*
     * 
     */
    public void print() {
    	System.out.print(this.puzzle[0]+"  "+this.puzzle[1]+"  "+this.puzzle[2]+"    "+this.puzzle[3]+"  "+this.puzzle[4]+"  "+this.puzzle[5]+"    "+this.puzzle[6]+"  "+this.puzzle[7]+"  "+this.puzzle[8]+"\n");
   	 System.out.print(this.puzzle[9]+"  "+this.puzzle[10]+"  "+this.puzzle[11]+"    "+this.puzzle[12]+"  "+this.puzzle[13]+"  "+this.puzzle[14]+"    "+this.puzzle[15]+"  "+this.puzzle[16]+"  "+this.puzzle[17]+"\n");
   	 System.out.print(this.puzzle[18]+"  "+this.puzzle[19]+"  "+this.puzzle[20]+"    "+this.puzzle[21]+"  "+this.puzzle[22]+"  "+this.puzzle[23]+"    "+this.puzzle[24]+"  "+this.puzzle[25]+"  "+this.puzzle[26]+"\n\n");
   	 System.out.print(this.puzzle[27]+"  "+this.puzzle[28]+"  "+this.puzzle[29]+"    "+this.puzzle[30]+"  "+this.puzzle[31]+"  "+this.puzzle[32]+"    "+this.puzzle[33]+"  "+this.puzzle[34]+"  "+this.puzzle[35]+"\n");
   	 System.out.print(this.puzzle[36]+"  "+this.puzzle[37]+"  "+this.puzzle[38]+"    "+this.puzzle[39]+"  "+this.puzzle[40]+"  "+this.puzzle[41]+"    "+this.puzzle[42]+"  "+this.puzzle[43]+"  "+this.puzzle[44]+"\n");
   	 System.out.print(this.puzzle[45]+"  "+this.puzzle[46]+"  "+this.puzzle[47]+"    "+this.puzzle[48]+"  "+this.puzzle[49]+"  "+this.puzzle[50]+"    "+this.puzzle[51]+"  "+this.puzzle[52]+"  "+this.puzzle[53]+"\n\n");
   	 System.out.print(this.puzzle[54]+"  "+this.puzzle[55]+"  "+this.puzzle[56]+"    "+this.puzzle[57]+"  "+this.puzzle[58]+"  "+this.puzzle[59]+"    "+this.puzzle[60]+"  "+this.puzzle[61]+"  "+this.puzzle[62]+"\n");
   	 System.out.print(this.puzzle[63]+"  "+this.puzzle[64]+"  "+this.puzzle[65]+"    "+this.puzzle[66]+"  "+this.puzzle[67]+"  "+this.puzzle[68]+"    "+this.puzzle[69]+"  "+this.puzzle[70]+"  "+this.puzzle[71]+"\n");
   	 System.out.print(this.puzzle[72]+"  "+this.puzzle[73]+"  "+this.puzzle[74]+"    "+this.puzzle[75]+"  "+this.puzzle[76]+"  "+this.puzzle[77]+"    "+this.puzzle[78]+"  "+this.puzzle[79]+"  "+this.puzzle[80]+"\n");
    	
    }
    
    
    public void printPuzzle(int[] sudoku) {
    	System.out.print(sudoku[0]+"  "+sudoku[1]+"  "+sudoku[2]+"    "+sudoku[3]+"  "+sudoku[4]+"  "+sudoku[5]+"    "+sudoku[6]+"  "+sudoku[7]+"  "+sudoku[8]+"\n");
      	 System.out.print(sudoku[9]+"  "+sudoku[10]+"  "+sudoku[11]+"    "+sudoku[12]+"  "+sudoku[13]+"  "+sudoku[14]+"    "+sudoku[15]+"  "+sudoku[16]+"  "+sudoku[17]+"\n");
      	 System.out.print(sudoku[18]+"  "+sudoku[19]+"  "+sudoku[20]+"    "+sudoku[21]+"  "+sudoku[22]+"  "+sudoku[23]+"    "+sudoku[24]+"  "+sudoku[25]+"  "+sudoku[26]+"\n\n");
      	 System.out.print(sudoku[27]+"  "+sudoku[28]+"  "+sudoku[29]+"    "+sudoku[30]+"  "+sudoku[31]+"  "+sudoku[32]+"    "+sudoku[33]+"  "+sudoku[34]+"  "+sudoku[35]+"\n");
      	 System.out.print(sudoku[36]+"  "+sudoku[37]+"  "+sudoku[38]+"    "+sudoku[39]+"  "+sudoku[40]+"  "+sudoku[41]+"    "+sudoku[42]+"  "+sudoku[43]+"  "+sudoku[44]+"\n");
      	 System.out.print(sudoku[45]+"  "+sudoku[46]+"  "+sudoku[47]+"    "+sudoku[48]+"  "+sudoku[49]+"  "+sudoku[50]+"    "+sudoku[51]+"  "+sudoku[52]+"  "+sudoku[53]+"\n\n");
      	 System.out.print(sudoku[54]+"  "+sudoku[55]+"  "+sudoku[56]+"    "+sudoku[57]+"  "+sudoku[58]+"  "+sudoku[59]+"    "+sudoku[60]+"  "+sudoku[61]+"  "+sudoku[62]+"\n");
      	 System.out.print(sudoku[63]+"  "+sudoku[64]+"  "+sudoku[65]+"    "+sudoku[66]+"  "+sudoku[67]+"  "+sudoku[68]+"    "+sudoku[69]+"  "+sudoku[70]+"  "+sudoku[71]+"\n");
      	 System.out.print(sudoku[72]+"  "+sudoku[73]+"  "+sudoku[74]+"    "+sudoku[75]+"  "+sudoku[76]+"  "+sudoku[77]+"    "+sudoku[78]+"  "+sudoku[79]+"  "+sudoku[80]+"\n");
       
    }
    

    
}