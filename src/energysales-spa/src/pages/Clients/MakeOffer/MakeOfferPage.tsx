import {Badge, Button, Card, Group, Slider, Stack, Text, useMantineTheme} from '@mantine/core';
import {useMakeOfferPage} from './useMakeOfferPage';
import {toast} from "react-toastify";

export function MakeOfferPage() {
    const {
        isPending,
        error,
        client,
        services,
        selectedService,
        setSelectedService,
        expandedService,
        setExpandedService,
        handlerMakeOffer,
        handleSendOfferEmail,
        dueInDays,
        setDueInDays,
        offerResponse
    } = useMakeOfferPage();

    const handleServiceClick = (serviceId: string) => {
        setExpandedService(serviceId === expandedService ? null : serviceId);
    };

    const handleSelectService = (serviceId: string) => {
        setSelectedService(serviceId);
        setExpandedService(null);
    };

    const handleDeselectService = () => {
        setSelectedService("");
    };

    const handleCopyToClipboard = (url: string) => {
        navigator.clipboard.writeText(url);
        toast.success("Link copied to clipboard");
    };

    const theme = useMantineTheme();

    if (!client) {
        return <p>Loading...</p>;
    }

    return (
        <div>
            <Group h="100%" px="md" justify="space-between">
                <Group>
                    <h1>Make Offer</h1>
                </Group>

                <Group>
                    <Card shadow="sm" padding="lg" style={{maxWidth: 300}}>
                        <Group style={{marginBottom: 5}}>
                            <Badge color="blue" variant="light">
                                {client.name}
                            </Badge>
                        </Group>

                        <Text size="sm" style={{color: theme.colors.gray[7], lineHeight: 1.5}}>
                            <strong>Location: </strong>{client.location.district}
                        </Text>
                        <Text size="sm" style={{color: theme.colors.gray[7], lineHeight: 1.5}}>
                            <strong>E-mail: </strong>{client.email}
                        </Text>
                        <Text size="sm" style={{color: theme.colors.gray[7], lineHeight: 1.5}}>
                            <strong>Phone: </strong>{client.phone}
                        </Text>
                        <Text size="sm" style={{color: theme.colors.gray[7], lineHeight: 1.5}}>
                            <strong>Nif: </strong>{client.nif}
                        </Text>

                    </Card>
                </Group>
            </Group>

            <h3>Select Service</h3>
            <div>
                {services?.map((service) => (
                    (selectedService === null || selectedService === service.id) && (
                        <Card
                            key={service.id}
                            shadow="sm"
                            padding="xs"
                            style={{
                                cursor: 'pointer',
                                border: selectedService === service.id ? '2px solid blue' : '',
                                marginBottom: '10px',
                            }}
                            onClick={() => handleServiceClick(service.id)}
                        >
                            <h4>{service.name}</h4>
                            {expandedService === service.id && (
                                <div>
                                    <p>Description: {service.description}</p>
                                    <p>Cycle Name: {service.cycleName}</p>
                                    <p>Cycle Type: {service.cycleType}</p>
                                    <p>Period Name: {service.periodName}</p>
                                    <p>Period Num Periods: {service.periodNumPeriods}</p>
                                    <h5>Price Details:</h5>
                                    <ul>
                                        <li>Ponta: {service.price.ponta}</li>
                                        <li>Cheia: {service.price.cheia}</li>
                                        <li>Vazio: {service.price.vazio}</li>
                                        <li>Super Vazio: {service.price.superVazio}</li>
                                        <li>Operador Mercado: {service.price.operadorMercado}</li>
                                        <li>GDO: {service.price.gdo}</li>
                                        <li>OMIP: {service.price.omip}</li>
                                        <li>Margem: {service.price.margem}</li>
                                    </ul>
                                    <Button onClick={() => handleSelectService(service.id)}>
                                        Select Service
                                    </Button>
                                </div>
                            )}
                        </Card>
                    )
                ))}
            </div>

            {selectedService && (
                <div>
                    <Button onClick={handleDeselectService}>
                        Deselect Service
                    </Button>
                    <h3>Due Date till (in days)</h3>
                    <Stack justify="center" gap="xs">
                        <Slider
                            value={dueInDays}
                            onChange={setDueInDays}
                            min={1}
                            max={90}
                            step={1}
                            label={(value) => value == 1 ? `${value} day` : `${value} days`}
                            mt="md"
                        />
                    </Stack>
                </div>
            )}

            {offerResponse ? (
                <div>
                    <h3>Offer Link</h3>
                    <p>{offerResponse.url}</p>
                    <Group mt="md">
                        <Button onClick={() => handleCopyToClipboard(offerResponse.url)}>
                            Copy to Clipboard
                        </Button>
                        <Button onClick={handleSendOfferEmail} disabled={!selectedService}>
                            Send to Client
                        </Button>
                        <Button component="a" href={offerResponse.url} target="_blank">
                            Open Link
                        </Button>
                    </Group>
                </div>
            ) : (
                <Group mt="md">
                    <Button onClick={handlerMakeOffer} disabled={!selectedService}>
                        Generate Offer Link
                    </Button>

                </Group>
            )}

            {isPending && <p>Loading...</p>}
            {error && <p>{error}</p>}
        </div>
    );
}