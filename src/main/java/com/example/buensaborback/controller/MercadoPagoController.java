package com.example.buensaborback.controller;

import com.example.buensaborback.domain.entities.Pedido;
import com.example.buensaborback.domain.entities.mp.PreferenceMP;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "api/mp")
public class MercadoPagoController {

    @Value("${front.clienteUrl}")
    private String frontClientUrl;

    @Value("${mp.accessToken}")
    private String tokenAcceso;

    @PostMapping("create_preference_mp")
    public PreferenceMP getPreferenciaIdMercadoPago(@RequestBody Pedido pedido) {
        try {
            MercadoPagoConfig.setAccessToken(tokenAcceso);
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(String.valueOf(pedido.getId()))
                    .title(pedido.getSucursal().getEmpresa().getNombre())
                    .description("Pedido realizado desde el carrito de compras")
                    .pictureUrl(pedido.getSucursal().getEmpresa().getImagen().getUrl())
                    .quantity(1)
                    .currencyId("ARG")
                    .unitPrice(BigDecimal.valueOf(pedido.getTotal()))
                    .build();
            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            PreferenceBackUrlsRequest backURL = PreferenceBackUrlsRequest.builder()
                    .success(frontClientUrl + "/mpsuccess/" + pedido.getId())
                    .pending(frontClientUrl + "/mppending/" + pedido.getId())
                    .failure(frontClientUrl + "/mpfailure/" + pedido.getId()).build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backURL)
                    .build();
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            PreferenceMP mpPreference = new PreferenceMP();
            mpPreference.setStatusCode(preference.getResponse().getStatusCode());
            mpPreference.setId(preference.getId());
            return mpPreference;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}

