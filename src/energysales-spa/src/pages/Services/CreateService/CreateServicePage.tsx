import {CreateServiceInputModel} from '../../../services/models/ServiceModel'
import {Button, Group, TextInput} from "@mantine/core"
import React, {useState} from "react"
import {useCreateServicePage} from "./useCreateServicePage.tsx"

export function CreateServicePage() {
    const {
        createService,
        isFetching,
        error,
        onCreateServiceButtonClick
    } = useCreateServicePage();

    const [serviceName, setServiceName] = useState('');
    const [serviceDescription, setServiceDescription] = useState('');
    const [price, setPrice] = useState({
        ponta: 0,
        cheia: 0,
        vazio: 0,
        superVazio: 0,
        operadorMercado: 0,
        gdo: 0,
        omip: 0,
        margem: 0
    });

    const handleCreateService = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const input: CreateServiceInputModel = {
            name: serviceName,
            description: serviceDescription,
            cycleName: 'cycleName',
            cycleType: 'cycleType',
            periodName: 'periodName',
            periodNumPeriods: 1,
            price: price,
        };
        await createService(input);
        setServiceName('');
        setServiceDescription('');
        setPrice({
            ponta: 0,
            cheia: 0,
            vazio: 0,
            superVazio: 0,
            operadorMercado: 0,
            gdo: 0,
            omip: 0,
            margem: 0
        });
    };

    return (
        <div>
            <h1>Create Service</h1>
            <form onSubmit={(e) => handleCreateService(e)}>
                <TextInput
                    label="Service Name"
                    placeholder="Enter service name"
                    value={serviceName}
                    onChange={(e) => setServiceName(e.currentTarget.value)}
                    required
                />
                <TextInput
                    label="Description"
                    placeholder="Enter description"
                    value={serviceDescription}
                    onChange={(e) => setServiceDescription(e.currentTarget.value)}
                    required
                />
                <TextInput
                    label="Ponta Price"
                    placeholder="Enter ponta price"
                    type="number"
                    value={price.ponta}
                    onChange={(e) => setPrice({...price, ponta: parseFloat(e.currentTarget.value)})}
                    required
                />
                {/* Add more TextInput components for each price field as needed */}
                <TextInput
                    label="Cheia Price"
                    placeholder="Enter cheia price"
                    type="number"
                    value={price.cheia}
                    onChange={(e) => setPrice({...price, cheia: parseFloat(e.currentTarget.value)})}
                    required
                />
                <TextInput
                    label="Vazio Price"
                    placeholder="Enter vazio price"
                    type="number"
                    value={price.vazio}
                    onChange={(e) => setPrice({...price, vazio: parseFloat(e.currentTarget.value)})}
                    required
                />
                <TextInput
                    label="Super Vazio Price"
                    placeholder="Enter super vazio price"
                    type="number"
                    value={price.superVazio}
                    onChange={(e) => setPrice({...price, superVazio: parseFloat(e.currentTarget.value)})}
                    required
                />
                <TextInput
                    label="Operador Mercado Price"
                    placeholder="Enter operador mercado price"
                    type="number"
                    value={price.operadorMercado}
                    onChange={(e) => setPrice({...price, operadorMercado: parseFloat(e.currentTarget.value)})}
                    required
                />
                <TextInput
                    label="GDO Price"
                    placeholder="Enter gdo price"
                    type="number"
                    value={price.gdo}
                    onChange={(e) => setPrice({...price, gdo: parseFloat(e.currentTarget.value)})}
                    required
                />
                <TextInput
                    label="OMIP Price"
                    placeholder="Enter omip price"
                    type="number"
                    value={price.omip}
                    onChange={(e) => setPrice({...price, omip: parseFloat(e.currentTarget.value)})}
                    required
                />
                <TextInput
                    label="Margem Price"
                    placeholder="Enter margem price"
                    type="number"
                    value={price.margem}
                    onChange={(e) => setPrice({...price, margem: parseFloat(e.currentTarget.value)})}
                    required
                />
                <Group mt="md">
                    <Button type="submit">Create</Button>
                </Group>
            </form>

            {isFetching && <p>Loading...</p>}
            {error && <p>{error}</p>}
        </div>
    );
}