package com.sori.touchsori.utill;

/**
 * Created by sgkim on 2017-03-14.
 */

// 2017.04.04 version
public class NFD2
{
    private double wtb[];

    public NFD2() {
        if(wtb == null)
            wtb = new double[2063];

        final int[] ntryh= new int[] {4, 2, 3, 5};
        double  argh;
        int     ntry=0, i, j;
        double  argld;
        int     k1, l1, l2, ib;
        double  fi;
        int     ld, ii, nf, ip, nl, is, nq, nr;
        double  arg;
        int     ido, ipm;
        int     nfm1;

        nl=1024;
        nf=0;
        j=0;

        factorize_loop:
        while(true) {
            ++j;
            if(j<=4)
                ntry=ntryh[j-1];
            else
                ntry+=2;
            do {
                nq=nl / ntry;
                nr=nl-ntry*nq;
                if(nr !=0) continue factorize_loop;
                ++nf;
                wtb[nf+2049]=ntry;

                nl=nq;
                if(ntry==2 && nf !=1) {
                    for(i=2; i<=nf; i++) {
                        ib=nf-i+2;
                        wtb[ib+2049]=wtb[ib+2048];
                    }
                    wtb[2050]=2;
                }
            }while(nl !=1);
            break factorize_loop;
        }
        wtb[2048] = 1024;
        wtb[2049] = nf;
        argh=6.28318530717959 /(double)(1024);
        is=0;
        nfm1=nf-1;
        l1=1;
        if(nfm1==0) return;
        for(k1=1; k1<=nfm1; k1++) {
            ip=(int)wtb[k1+2049];
            ld=0;
            l2=l1*ip;
            ido=1024 / l2;
            ipm=ip-1;
            for(j=1; j<=ipm;++j) {
                ld+=l1;
                i=is;
                argld=(double)ld*argh;

                fi=0;
                for(ii=3; ii<=ido; ii+=2) {
                    i+=2;
                    fi+=1;
                    arg=fi*argld;
                    wtb[i+1022] = Math.cos(arg);
                    wtb[i+1023] = Math.sin(arg);
                }
                is+=ido;
            }
            l1=l2;
        }
    }

    public void ft(double c[])
    {
        int     k1, l1, l2, na, nf, ip, iw, ido;

        double[] ch = new double[1024];
        System.arraycopy(wtb, 0, ch, 0, 1024);

        nf=(int)wtb[2049];
        na=1;
        l2=1024;
        iw=2047;
        for(k1=1; k1<=nf;++k1)
        {
            ip=(int)wtb[nf-k1+2050];
            l1=l2 / ip;
            ido=1024 / l2;
            iw-=(ip-1)*ido;
            na=1-na;

            if(na==0)
                ft1(ido, l1, c, ch, wtb, iw);
            else
                ft1(ido, l1, ch, c, wtb, iw);

            l2=l1;
        }
        if(na==1) return;
//        for(int i=0; i<1024; i++) c[i]=ch[i];
        System.arraycopy(ch, 0, c, 0, 1024);
    }


    void ft1(int ido, int l1, final double cc[], double ch[], final double wb[], int offset)
    {
        final double hsqt2=0.7071067811865475D;
        int i, k, ic;
        double  ci2, ci3, ci4, cr2, cr3, cr4, ti1, ti2, ti3, ti4, tr1, tr2, tr3, tr4;
        int iw1, iw2, iw3;
        iw1 = offset;
        iw2 = offset + ido;
        iw3 = iw2 + ido;
        for(k=0; k<l1; k++)
        {
            tr1=cc[(k+l1)*ido]+cc[(k+3*l1)*ido];
            tr2=cc[k*ido]+cc[(k+2*l1)*ido];
            ch[4*k*ido]=tr1+tr2;
            ch[ido-1+(4*k+3)*ido]=tr2-tr1;
            ch[ido-1+(4*k+1)*ido]=cc[k*ido]-cc[(k+2*l1)*ido];
            ch[(4*k+2)*ido]=cc[(k+3*l1)*ido]-cc[(k+l1)*ido];
        }
        if(ido<2) return;
        if(ido !=2)
        {
            for(k=0; k<l1; k++)
            {
                for(i=2; i<ido; i+=2)
                {
                    ic=ido-i;
                    cr2 = wb[i-2+iw1]*cc[i-1+(k+l1)*ido]+wb[i-1+iw1]*cc[i+(k+l1)*ido];
                    ci2 = wb[i-2+iw1]*cc[i+(k+l1)*ido]-wb[i-1+iw1]*cc[i-1+(k+l1)*ido];
                    cr3 = wb[i-2+iw2]*cc[i-1+(k+2*l1)*ido]+wb[i-1+iw2]*cc[i+(k+2*l1)*ido];
                    ci3 = wb[i-2+iw2]*cc[i+(k+2*l1)*ido]-wb[i-1+iw2]*cc[i-1+(k+2*l1)*ido];
                    cr4 = wb[i-2+iw3]*cc[i-1+(k+3*l1)*ido]+wb[i-1+iw3]*cc[i+(k+3*l1)*ido];
                    ci4 = wb[i-2+iw3]*cc[i+(k+3*l1)*ido]-wb[i-1+iw3]*cc[i-1+(k+3*l1)*ido];
                    tr1=cr2+cr4;
                    tr4=cr4-cr2;
                    ti1=ci2+ci4;
                    ti4=ci2-ci4;
                    ti2=cc[i+k*ido]+ci3;
                    ti3=cc[i+k*ido]-ci3;
                    tr2=cc[i-1+k*ido]+cr3;
                    tr3=cc[i-1+k*ido]-cr3;
                    ch[i-1+4*k*ido]=tr1+tr2;
                    ch[ic-1+(4*k+3)*ido]=tr2-tr1;
                    ch[i+4*k*ido]=ti1+ti2;
                    ch[ic+(4*k+3)*ido]=ti1-ti2;
                    ch[i-1+(4*k+2)*ido]=ti4+tr3;
                    ch[ic-1+(4*k+1)*ido]=tr3-ti4;
                    ch[i+(4*k+2)*ido]=tr4+ti3;
                    ch[ic+(4*k+1)*ido]=tr4-ti3;
                }
            }
            if(ido%2==1) return;
        }
        for(k=0; k<l1; k++)
        {
            ti1=-hsqt2*(cc[ido-1+(k+l1)*ido]+cc[ido-1+(k+3*l1)*ido]);
            tr1=hsqt2*(cc[ido-1+(k+l1)*ido]-cc[ido-1+(k+3*l1)*ido]);
            ch[ido-1+4*k*ido]=tr1+cc[ido-1+k*ido];
            ch[ido-1+(4*k+2)*ido]=cc[ido-1+k*ido]-tr1;
            ch[(4*k+1)*ido]=ti1-cc[ido-1+(k+2*l1)*ido];
            ch[(4*k+3)*ido]=ti1+cc[ido-1+(k+2*l1)*ido];
        }
    }
}
