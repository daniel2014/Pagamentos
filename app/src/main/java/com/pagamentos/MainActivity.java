package com.pagamentos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import br.com.uol.pslibs.checkout_in_app.PSCheckout;
import br.com.uol.pslibs.checkout_in_app.transparent.vo.PSCheckoutResponse;
import br.com.uol.pslibs.checkout_in_app.transparent.vo.PSTransparentDefaultRequest;
import br.com.uol.pslibs.checkout_in_app.wallet.util.PSCheckoutConfig;
import cn.carbs.android.library.MDDialog;

public class MainActivity extends AppCompatActivity implements Observer {

    private Product product;
    private br.com.uol.pslibs.checkout_in_app.transparent.listener.PSCheckoutListener psCheckoutListener = new br.com.uol.pslibs.checkout_in_app.transparent.listener.PSCheckoutListener() {
        @Override
        public void onSuccess(PSCheckoutResponse psCheckoutResponse) {
            Toast.makeText(MainActivity.this, "Sucesso: " + psCheckoutResponse.getMessage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "Código: " + psCheckoutResponse.getCode(), Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "Status: " + psCheckoutResponse.getStatus(), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onFailure(PSCheckoutResponse psCheckoutResponse) {
            Toast.makeText(MainActivity.this, "Erro: " + psCheckoutResponse.getMessage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "Erro: " + psCheckoutResponse.getCode(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProcessing() {
            Toast.makeText(MainActivity.this, "Processando pagamento...", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initProduct();
        initViews(product);
        //Inicialização a lib com parametros necessarios
        PSCheckoutConfig psCheckoutConfig = new PSCheckoutConfig();
        psCheckoutConfig.setSellerEmail("EMAIL DA CONTA DO VENDEDOR");
        psCheckoutConfig.setSellerToken("TOKEN DA CONTA");
//Informe o fragment container
        psCheckoutConfig.setContainer(R.id.conatierfragment);

//Inicializa apenas os recursos de pagamento transparente e boleto
        PSCheckout.init(this, psCheckoutConfig);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PSCheckout.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private void initProduct() {
        product = new Product(
                "6658-3324599755412",
                "TÊNIS ADIDAS BARRICADE COURT 2",
                "Adiwear: Borracha de altíssima durabilidade que permite que a sola não marque o solo./ Adiprene +: Protege a parte dianteira do pé proporcionando./ Adiprene: Proporciona alta absorção de impactos para amortecer e proteger o calcanhar.",
                3,
                69.90,
                R.mipmap.tennis);
    }

    private void initViews(Product product) {
        ((ImageView) findViewById(R.id.img)).setImageResource(product.getImg());
        ((TextView) findViewById(R.id.name)).setText(product.getName());
        ((TextView) findViewById(R.id.description)).setText(product.getDescription());
        ((TextView) findViewById(R.id.stock)).setText(product.getStockString());
        ((TextView) findViewById(R.id.price)).setText(product.getPriceString());
    }

    public void buy(View view) {
        new MDDialog.Builder(this)
                .setTitle("Pagamento")
                .setContentView(R.layout.payment)
                .setNegativeButton("Cancelar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setPositiveButton("Finalizar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View root = v.getRootView();
                        CreditCard creditCard = new CreditCard();
                        creditCard.setCardNumber(getViewContent(root, R.id.card_number));
                        creditCard.setName(getViewContent(root, R.id.name));
                        creditCard.setMonth(getViewContent(root, R.id.month));
                        creditCard.setYear(getViewContent(root, R.id.year));
                        creditCard.setCvv(getViewContent(root, R.id.cvv));
                        creditCard.setParcels(Integer.parseInt(getViewContent(root, R.id.parcels)));


                        PSTransparentDefaultRequest psTransparentDefaultRequest = new PSTransparentDefaultRequest();
                        psTransparentDefaultRequest
                                .setDocumentNumber("CPF DO COMPRADOR")
                                .setName(creditCard.getName())
                                .setEmail("EMAIL DO COMPRADOR")
                                .setAreaCode("AREA")
                                .setPhoneNumber("NUMERO DE TELEFONE")
                                .setStreet("ENDEREÇO")
                                .setAddressComplement("")
                                .setAddressNumber("NUMERO DA CASA")
                                .setDistrict("DISTRITO(ESTADO")
                                .setCity("CIDADE")
                                .setState("SP")
                                .setCountry("BRA")
                                .setPostalCode("CEP DO COMPRADOR")
                                .setTotalValue("VALOR TOTAL DA COMPRA")
                                .setAmount("VALOR DA PARCEA")
                                .setDescriptionPayment("Pagamento do teste de integração")
                                .setQuantity(creditCard.getParcels())
                                .setCreditCard(creditCard.getCardNumber())
                                .setCreditCardName(creditCard.getName())
                                .setCvv(creditCard.getCvv())
                                .setExpMonth(creditCard.getMonth())
                                .setExpYear(creditCard.getYear())
                                .setBirthDate("DATA DE NASCIMENTO DO COMPRADOR");

                        PSCheckout.payTransparentDefault(psTransparentDefaultRequest, psCheckoutListener, MainActivity.this);

                    }
                })
                .create()
                .show();
    }

    private String getViewContent(View root, int id) {
        EditText field = root.findViewById(id);
        return field.getText().toString();
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
