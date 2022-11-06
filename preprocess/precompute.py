# This script will get the latest 
# require pandas packages
import pandas as pd 
import os

my_path = os.path.abspath(os.path.dirname(__file__))
dataDir = os.path.join(my_path, "../project_files/data_files/")
outputDir = dataDir
# source file
order_path = dataDir + "order.csv"
order_line_path = dataDir + "order-line.csv"
district_path = dataDir + "district.csv"

# target file
order_lines_with_cid_path = outputDir+ "order_line.csv"
district_with_delivery_path = outputDir+ "district.csv"

def get_cid_in_order_line():
    
    if (os.path.exists(order_lines_with_cid_path)): 
        return

    O_C_ID_idx = 3
    W_ID_idx = 0
    D_ID_idx = 1
    O_ID_idx = 2

    orders = pd.read_csv(order_path, header = None)
    orders = orders.loc[:, [W_ID_idx, D_ID_idx, O_ID_idx, O_C_ID_idx]]
    order_lines = pd.read_csv(order_line_path, header = None)


    order_lines_with_cid = order_lines.merge(orders, on = [W_ID_idx, D_ID_idx, O_ID_idx])
    order_lines_with_cid.to_csv(order_lines_with_cid_path, header = False, index = False)

def compute_next_order_to_deliver():
    if (os.path.exists(district_with_delivery_path)): 
        return

    W_ID_idx = 0
    D_ID_idx = 1
    O_CARRIER_ID_idx = 4
    O_ID_idx = 2

    orders = pd.read_csv(order_path, header = None)

    district_with_next_delivery_id = orders.groupby([W_ID_idx, D_ID_idx], as_index = False)[O_ID_idx].min()

    districts = pd.read_csv(district_path, header = None)
    district_with_delivery = districts.merge(district_with_next_delivery_id, on = [W_ID_idx, D_ID_idx])

    district_with_delivery.to_csv(district_with_delivery_path, header = False, index = False)

get_cid_in_order_line()
compute_next_order_to_deliver()
