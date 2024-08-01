import {useServicePage} from './useServicePage.tsx'

export function ServicePage() {
    const {
        service,
        isLoadingService,
        error,
        editableService,
        handleInputChange,
        onEditServiceButtonClick,
        onUpdateServiceClick,
        onDeleteServiceClick
    } = useServicePage()

    if (error) return <p>Error loading service</p>
    if (isLoadingService) return <p>Loading...</p>

    return (
        <div>
            <h1>Service</h1>
            {editableService ? (
                <div>
                    <div>
                        <label>
                            Name:
                            <input type='text' name='name' value={editableService.name} onChange={handleInputChange}/>
                        </label>
                    </div>
                    <div>
                        <label>
                            Description:
                            <textarea name='description' value={editableService.description}
                                      onChange={handleInputChange}/>
                        </label>
                    </div>
                    <div>
                        <label>
                            Cycle Name:
                            <input type='text' name='cycleName' value={editableService.cycleName}
                                   onChange={handleInputChange}/>
                        </label>
                    </div>
                    <div>
                        <label>
                            Cycle Type:
                            <input type='text' name='cycleType' value={editableService.cycleType}
                                   onChange={handleInputChange}/>
                        </label>
                    </div>
                    <div>
                        <label>
                            Period Name:
                            <input type='text' name='periodName' value={editableService.periodName}
                                   onChange={handleInputChange}/>
                        </label>
                    </div>
                    <div>
                        <label>
                            Period Num Periods:
                            <input
                                type='number'
                                name='periodNumPeriods'
                                value={editableService.periodNumPeriods}
                                onChange={handleInputChange}
                            />
                        </label>
                    </div>
                    <div>
                        <label>Price:</label>
                        <div>
                            <label>
                                Ponta:
                                <input type='number' name='ponta' value={editableService.price.ponta}
                                       onChange={handleInputChange}/>
                            </label>
                        </div>
                        <div>
                            <label>
                                Cheia:
                                <input type='number' name='cheia' value={editableService.price.cheia}
                                       onChange={handleInputChange}/>
                            </label>
                        </div>
                        <div>
                            <label>
                                Vazio:
                                <input type='number' name='vazio' value={editableService.price.vazio}
                                       onChange={handleInputChange}/>
                            </label>
                        </div>
                        <div>
                            <label>
                                Super Vazio:
                                <input
                                    type='number'
                                    name='superVazio'
                                    value={editableService.price.superVazio}
                                    onChange={handleInputChange}
                                />
                            </label>
                        </div>
                        <div>
                            <label>
                                Operador Mercado:
                                <input
                                    type='number'
                                    name='operadorMercado'
                                    value={editableService.price.operadorMercado}
                                    onChange={handleInputChange}
                                />
                            </label>
                        </div>
                        <div>
                            <label>
                                GDO:
                                <input type='number' name='gdo' value={editableService.price.gdo}
                                       onChange={handleInputChange}/>
                            </label>
                        </div>
                        <div>
                            <label>
                                OMIP:
                                <input type='number' name='omip' value={editableService.price.omip}
                                       onChange={handleInputChange}/>
                            </label>
                        </div>
                        <div>
                            <label>
                                Margem:
                                <input type='number' name='margem' value={editableService.price.margem}
                                       onChange={handleInputChange}/>
                            </label>
                        </div>
                    </div>
                    <button onClick={() => onUpdateServiceClick()}>Save</button>
                </div>
            ) : (
                service && (
                    <div>
                        <p>Name: {service.name}</p>
                        <p>Description: {service.description}</p>
                        <p>Cycle Name: {service.cycleName}</p>
                        <p>Cycle Type: {service.cycleType}</p>
                        <p>Period Name: {service.periodName}</p>
                        <p>Period Num Periods: {service.periodNumPeriods}</p>
                        <div>
                            <p>Price:</p>
                            <p>Ponta: {service.price.ponta}</p>
                            <p>Cheia: {service.price.cheia}</p>
                            <p>Vazio: {service.price.vazio}</p>
                            <p>Super Vazio: {service.price.superVazio}</p>
                            <p>Operador Mercado: {service.price.operadorMercado}</p>
                            <p>GDO: {service.price.gdo}</p>
                            <p>OMIP: {service.price.omip}</p>
                            <p>Margem: {service.price.margem}</p>
                        </div>
                        <button onClick={onEditServiceButtonClick}>Edit</button>
                        <button onClick={() => onDeleteServiceClick(service)}>Delete</button>
                    </div>
                )
            )}
        </div>
    )
}
