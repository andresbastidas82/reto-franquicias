package com.pragma.franchise.infrastructure.adapters.persistenceadapter;

import com.pragma.franchise.domain.model.Product;
import com.pragma.franchise.domain.model.TopStockProduct;
import com.pragma.franchise.domain.spi.ProductPersistencePort;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper.ProductEntityMapper;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository.ProductRepository;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.resilience.ResilienceHelper;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class ProductPersistenceAdapter implements ProductPersistencePort {

    private final ProductRepository productRepository;
    private final ProductEntityMapper productEntityMapper;
    private final ResilienceHelper resilienceHelper;

    /**
     * Contador de llamadas para simular fallos en getTopStockProducts.
     * SOLO PARA PRUEBAS — eliminar antes de producción.
     */
    /*private final AtomicInteger callCounter = new AtomicInteger(0);*/

    @Override
    public Mono<Product> save(Product product) {
        return resilienceHelper.applyResilience(
                productRepository.save(productEntityMapper.toEntity(product))
                        .map(productEntityMapper::toModel));
    }

    @Override
    public Mono<Boolean> existsByNameAndBranchId(String name, Long branchId) {
        return resilienceHelper.applyResilience(
                productRepository.existsByNameAndBranchId(name, branchId));
    }

    @Override
    public Mono<Product> findById(Long id) {
        return resilienceHelper.applyResilience(
                productRepository.findById(id)
                        .map(productEntityMapper::toModel));
    }

    @Override
    public Mono<Void> deleteById(Long productId) {
        return resilienceHelper.applyResilience(
                productRepository.deleteById(productId));
    }

    @Override
    public Flux<TopStockProduct> getTopStockProducts(Long franchiseId) {
        return resilienceHelper.applyResilience(
                productRepository.findTopStockProductsByFranchise(franchiseId));
    }


    /**
     * ============================================================
     * MÉTODO DE PRUEBA PARA RESILIENCIA (timeout + bulkhead + circuit breaker)
     * ============================================================
     *
     * Simula fallos de BD usando un contador atómico.
     * Las primeras 4 llamadas lanzan RuntimeException.
     * A partir de la 5ta, la "BD se recupera" y responde normalmente.
     *
     * FLUJO ESPERADO DESDE POSTMAN (sliding-window-size: 5, threshold: 50%):
     *
     * — ESTADO CLOSED (circuito cerrado, todo pasa) —
     * Llamada 1: Error simulado → circuito registra fallo (1/5)
     * Llamada 2: Error simulado → circuito registra fallo (2/5)
     * Llamada 3: Error simulado → circuito registra fallo (3/5)
     * Llamada 4: Error simulado → circuito registra fallo (4/5)
     * Llamada 5: El circuito evalúa la ventana: 4 fallos de 5 = 80% > 50%
     *            → circuito se ABRE, responde 503 (CallNotPermittedException)
     *
     * — ESTADO OPEN (circuito abierto, rechaza todo sin tocar la BD) —
     * Llamadas 6+: Respuesta inmediata 503 durante 15 segundos
     *
     * — ESTADO HALF_OPEN (después de 15s, permite 3 llamadas de prueba) —
     * Llamadas de prueba: Como el contador ya superó 4, van a la BD real
     *                     y tienen éxito → circuito vuelve a CLOSED
     *
     * IMPORTANTE: Comentar este método y descomentar el original
     *             cuando se terminen las pruebas.
     * ============================================================
     */
    /*@Override
    public Flux<TopStockProduct> getTopStockProducts(Long franchiseId) {
        int currentCall = callCounter.incrementAndGet();

        // Las primeras 4 llamadas simulan que la BD está caída
        if (currentCall <= 4) {
            return resilienceHelper.applyResilience(
                    Flux.error(new RuntimeException(
                            "Simulated DB failure - call #" + currentCall
                            + " (fallo " + currentCall + "/5 en ventana)")));
        }

        // A partir de la 5ta llamada, la BD "se recupera"
        // En estado OPEN esto no se ejecuta (el circuit breaker rechaza antes)
        // En estado HALF_OPEN o CLOSED, ejecuta la query real
        return resilienceHelper.applyResilience(
                productRepository.findTopStockProductsByFranchise(franchiseId));
    }*/
}
